package ca.wlu.gisql.environment.parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.wlu.gisql.interactome.Complement;
import ca.wlu.gisql.interactome.ComputedInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.binary.Intersection;
import ca.wlu.gisql.interactome.binary.Union;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;
import ca.wlu.gisql.util.ToStringComparator;

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

	public static AstNode makeConjunct(AstNode a, AstNode b) {
		if (a.equals(b)) {
			return a;
		} else {
			return new AstLogic(a, b, Operation.Conjunct);
		}
	}

	public static AstNode makeDisjunct(AstNode a, AstNode b) {
		if (a.equals(b)) {
			return a;
		} else {
			return new AstLogic(a, b, Operation.Disjunct);
		}
	};

	public static AstNode makeNegation(AstNode a) {
		if (a instanceof AstLogic) {
			AstLogic n = (AstLogic) a;
			if (n.operation == Operation.Negation) {
				return n.left;
			}
		}
		return new AstLogic(a, null, Operation.Negation);
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

			List<Integer> term = productOfSums.get(productOfSums.size() - 1);
			int index = termini.indexOf(node);
			if (!term.contains(index)) {
				term.add(index);
			}
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

	private final Operation operation;

	private AstLogic(AstNode left, AstNode right, Operation operation) {
		this.left = left;
		this.right = right;
		this.operation = operation;
	}

	public Interactome asInteractome() {
		AstNode baseNormalForm = distributeDisjunctOf(this.removeNegation());
		if (baseNormalForm instanceof AstLogic) {
			AstLogic normalForm = (AstLogic) baseNormalForm;

			List<List<Integer>> productOfSums = new ArrayList<List<Integer>>();
			List<List<Integer>> productOfSumsNegated = new ArrayList<List<Integer>>();
			AstList termini = new AstList();
			normalForm.prepareInteractomes(termini);
			Collections.sort(termini, ToStringComparator.instance);
			normalForm.makeTermMatrix(productOfSums, productOfSumsNegated,
					termini, Operation.Conjunct);
			List<Interactome> interactomes = termini.asInteractomeList();

			for (int index = 0; index < productOfSums.size(); index++) {
				for (int subindex = index + 1; subindex < productOfSums.size(); subindex++) {
					if (productOfSums.get(index).equals(
							productOfSums.get(subindex))
							&& productOfSumsNegated.get(index).equals(
									productOfSumsNegated.get(subindex))) {
						productOfSums.remove(subindex);
						productOfSumsNegated.remove(subindex);
					}
				}
				Collections.sort(productOfSums.get(index));
				Collections.sort(productOfSumsNegated.get(index));
			}
			quicksort(productOfSums, productOfSumsNegated, 0, productOfSums
					.size() - 1);
			return new ComputedInteractome(interactomes, productOfSums,
					productOfSumsNegated);
		} else {
			return baseNormalForm.asInteractome();
		}
	}

	private AstNode distributeDisjunct() {
		if (this.operation == Operation.Disjunct) {
			if (operationOf(right) == Operation.Conjunct) {
				if (left.equals(((AstLogic) right).left)
						|| left.equals(((AstLogic) right).right)) {
					return distributeDisjunctOf(left);
				} else {
					return makeConjunct(distributeDisjunctOf(makeDisjunct(left,
							((AstLogic) right).left)),
							distributeDisjunctOf(makeDisjunct(left,
									((AstLogic) right).right)));
				}
			} else if (operationOf(left) == Operation.Conjunct) {
				if (right.equals(((AstLogic) left).left)
						|| right.equals(((AstLogic) left).right)) {
					return distributeDisjunctOf(right);
				} else {
					return makeConjunct(distributeDisjunctOf(makeDisjunct(
							right, ((AstLogic) left).left)),
							distributeDisjunctOf(makeDisjunct(right,
									((AstLogic) left).right)));

				}
			}

			return makeDisjunct(distributeDisjunctOf(left),
					distributeDisjunctOf(right));
		} else if (this.operation == Operation.Conjunct) {
			return makeConjunct(distributeDisjunctOf(left),
					distributeDisjunctOf(right));
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
				: right.fork(substitute)), operation);
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

	private void quicksort(List<List<Integer>> list1,
			List<List<Integer>> list2, int left, int right) {
		if (right > left) {
			int pivotIndex = left;
			String pivotValue1 = list1.get(pivotIndex).toString();
			String pivotValue2 = list1.get(pivotIndex).toString();
			swap(list1, list2, pivotIndex, right);
			int storeIndex = left;
			for (int i = left; i < right; i++) {
				String value1 = list1.get(i).toString();
				String value2 = list2.get(i).toString();
				int comparison = value1.compareTo(pivotValue1);
				if (comparison == 0)
					comparison = value2.compareTo(pivotValue2);
				if (comparison < 0) {
					swap(list1, list2, i, storeIndex);
					storeIndex++;
				}
			}
			swap(list1, list2, storeIndex, right);
			quicksort(list1, list2, left, storeIndex - 1);
			quicksort(list1, list2, storeIndex + 1, right);
		}
	}

	private AstNode removeNegation() {
		if (this.operation == Operation.Negation) {
			Operation suboperation = operationOf(left);
			if (suboperation == Operation.Negation) {
				return removeNegationOf(((AstLogic) left).left);
			} else if (suboperation == Operation.Conjunct) {
				return makeDisjunct(removeNegationOf(makeNegation(left)),
						removeNegationOf(makeNegation(right)));
			} else if (suboperation == Operation.Disjunct) {
				return makeConjunct(removeNegationOf(makeNegation(left)),
						removeNegationOf(makeNegation(right)));
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

	private void swap(List<List<Integer>> list1, List<List<Integer>> list2,
			int left, int right) {
		List<Integer> value1 = list1.get(left);
		list1.set(left, list1.get(right));
		list1.set(right, value1);

		List<Integer> value2 = list2.get(left);
		list2.set(left, list2.get(right));
		list2.set(right, value2);
	}

	public String toString() {
		return ShowableStringBuilder.toString(this);
	}
}
