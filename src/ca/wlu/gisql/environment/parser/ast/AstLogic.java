package ca.wlu.gisql.environment.parser.ast;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.Complement;
import ca.wlu.gisql.interactome.ComputedInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.binary.Intersection;
import ca.wlu.gisql.interactome.binary.Union;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class AstLogic implements AstNode {

	private enum Operation {
		Conjunct, Disjunct, Negation
	}

	private static AstNode distributeDisjunctOf(AstNode node) {
		if (node instanceof AstLogic) {
			return ((AstLogic) node).distributeDisjunct();
		} else {
			return node;
		}
	}

	public static AstLogic makeConjunct(AstNode a, AstNode b,
			TriangularNorm norm) {
		return new AstLogic(a, b, Operation.Conjunct, norm);

	}

	public static AstLogic makeDisjunct(AstNode a, AstNode b,
			TriangularNorm norm) {
		return new AstLogic(a, b, Operation.Disjunct, norm);
	};

	public static AstLogic makeNegation(AstNode a, TriangularNorm norm) {
		return new AstLogic(a, null, Operation.Negation, norm);
	}

	private static void makeTermMatrixOf(AstNode node,
			List<List<Integer>> productOfSums,
			List<List<Integer>> productOfSumsNegated, List<AstNode> termini,
			Operation parent) {
		if (node instanceof AstLogic) {
			((AstLogic) node).makeTermMatrix(productOfSums,
					productOfSumsNegated, termini, parent);

		} else {
			prepareMatrix(productOfSums, productOfSumsNegated, parent, null);

			productOfSums.get(productOfSums.size() - 1).add(
					termini.indexOf(node));
		}
	}

	private static Operation operationOf(AstNode node) {
		if (node instanceof AstLogic) {
			return ((AstLogic) node).operation;
		} else {
			return null;
		}
	}

	private static void prepareMatrix(List<List<Integer>> productOfSums,
			List<List<Integer>> productOfSumsNegated, Operation parent,
			Operation operation) {
		if (parent == Operation.Conjunct && operation != Operation.Conjunct) {
			productOfSums.add(new ArrayList<Integer>());
			productOfSumsNegated.add(new ArrayList<Integer>());
		}
	}

	private static AstNode removeNegationOf(AstNode node) {
		if (node instanceof AstLogic) {
			return ((AstLogic) node).removeNegation();
		} else {
			return node;
		}
	}

	private final AstNode left, right;

	private final TriangularNorm norm;

	private final Operation operation;

	private AstLogic(AstNode left, AstNode right, Operation operation,
			TriangularNorm norm) {
		this.left = left;
		this.right = right;
		this.operation = operation;
		this.norm = norm;
	}

	public Interactome asInteractome() {
		AstNode baseNormalForm = distributeDisjunctOf(this.removeNegation());
		if (baseNormalForm instanceof AstLogic) {
			AstLogic normalForm = (AstLogic) baseNormalForm;

			List<List<Integer>> productOfSums = new ArrayList<List<Integer>>();
			List<List<Integer>> productOfSumsNegated = new ArrayList<List<Integer>>();
			AstList termini = new AstList();
			normalForm.prepareInteractomes(termini);
			normalForm.makeTermMatrix(productOfSums, productOfSumsNegated,
					termini, Operation.Conjunct);
			List<Interactome> interactomes = termini.asInteractomeList();

			return new ComputedInteractome(norm, interactomes, productOfSums,
					productOfSumsNegated);
		} else {
			return baseNormalForm.asInteractome();
		}
	}

	private AstLogic distributeDisjunct() {
		if (this.operation == Operation.Disjunct) {
			if (operationOf(right) == Operation.Conjunct) {
				return makeConjunct(makeDisjunct(((AstLogic) right).left, left,
						norm).distributeDisjunct(), makeDisjunct(
						((AstLogic) right).right, right, norm), norm);

			} else if (operationOf(left) == Operation.Conjunct) {
				return makeConjunct(makeDisjunct(((AstLogic) left).left, right,
						norm).distributeDisjunct(), makeDisjunct(
						((AstLogic) left).right, right, norm), norm);

			}

			return makeDisjunct(distributeDisjunctOf(left),
					distributeDisjunctOf(right), norm);
		} else if (this.operation == Operation.Conjunct) {
			return makeConjunct(distributeDisjunctOf(left),
					distributeDisjunctOf(right), norm);
		} else {
			return this;
		}
	}

	public boolean equals(Object other) {
		if (other instanceof AstLogic) {
			AstLogic logic = ((AstLogic) other);
			return (this.operation == logic.operation)
					&& (this.left.equals(logic.left))
					&& (this.right == null ? logic.right == null : right
							.equals(logic.right));
		} else {
			return false;
		}
	}

	public AstNode fork(AstNode substitute) {
		return new AstLogic(left.fork(substitute), (right == null ? null
				: right.fork(substitute)), operation, norm);
	}

	public int getPrecedence() {
		switch (operation) {
		case Negation:
			return Complement.descriptor.getPrecedence();
		case Conjunct:
			return Intersection.descriptor.getPrecedence();
		case Disjunct:
			return Union.descriptor.getPrecedence();
		}
		return 0;
	}

	public boolean isInteractome() {
		return true;
	}

	private void makeTermMatrix(List<List<Integer>> productOfSums,
			List<List<Integer>> productOfSumsNegated, List<AstNode> termini,
			Operation parent) {
		prepareMatrix(productOfSums, productOfSumsNegated, parent,
				this.operation);
		switch (this.operation) {
		case Disjunct:
			makeTermMatrixOf(left, productOfSums, productOfSumsNegated,
					termini, this.operation);
			makeTermMatrixOf(right, productOfSums, productOfSumsNegated,
					termini, this.operation);
			break;
		case Conjunct:
			makeTermMatrixOf(left, productOfSums, productOfSumsNegated,
					termini, this.operation);
			makeTermMatrixOf(right, productOfSums, productOfSumsNegated,
					termini, this.operation);
			break;
		case Negation:
			makeTermMatrixOf(left, productOfSumsNegated, productOfSums,
					termini, this.operation);
		}
	}

	private void prepareInteractomes(List<AstNode> termini) {
		if (left instanceof AstLogic) {
			((AstLogic) left).prepareInteractomes(termini);
		} else if (!termini.contains(left)) {
			termini.add(left);
		}

		if (right == null) {
			return;
		} else if (right instanceof AstLogic) {
			((AstLogic) right).prepareInteractomes(termini);
		} else if (!termini.contains(right)) {
			termini.add(right);
		}
	}

	private AstNode removeNegation() {
		if (this.operation == Operation.Negation) {
			Operation suboperation = operationOf(left);
			if (suboperation == Operation.Negation) {
				return removeNegationOf(((AstLogic) left).left);
			} else if (suboperation == Operation.Conjunct) {
				return makeDisjunct(makeNegation(left, norm).removeNegation(),
						makeNegation(right, norm).removeNegation(), norm);
			} else if (suboperation == Operation.Disjunct) {
				return makeConjunct(makeNegation(left, norm).removeNegation(),
						makeNegation(right, norm).removeNegation(), norm);
			}
		}
		return this;
	}

	public void show(ShowablePrintWriter print) {
		switch (operation) {
		case Negation:
			print.print("!");
			print.print(left, Complement.descriptor.getPrecedence());
			break;
		case Conjunct:
			print.print("(");
			print.print(left, Intersection.descriptor.getPrecedence());
			print.print(" & ");
			print.print(right, Intersection.descriptor.getPrecedence());
			print.print(")");
			break;
		case Disjunct:
			print.print("(");
			print.print(left, Union.descriptor.getPrecedence());
			print.print(" | ");
			print.print(right, Union.descriptor.getPrecedence());
			print.print(")");
		}
	}

	public String toString() {
		return ShowableStringBuilder.toString(this);
	}
}
