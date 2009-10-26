package ca.wlu.gisql.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.logic.Complement;
import ca.wlu.gisql.interactome.logic.ComputedInteractome;
import ca.wlu.gisql.interactome.logic.Intersection;
import ca.wlu.gisql.interactome.logic.Union;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ToStringComparator;
import ca.wlu.gisql.vm.InstructionConstruct;
import ca.wlu.gisql.vm.InstructionPack;
import ca.wlu.gisql.vm.InstructionPush;

/**
 * Represents any set operation on {@link Interactome}s. All set operations are
 * broken down into conjunction(intersection), disjunction(union), and negation.
 * All operations are converted into conjunctive normal form.
 */
public class AstLogic extends AstNode {

	private enum Operation {
		Conjunct, Disjunct, Negation
	}

	/** Convience function for normalization. */
	private static AstNode distributeDisjunctOf(AstNode node) {
		if (node instanceof AstLogic) {
			return ((AstLogic) node).distributeDisjunct();
		} else {
			return node;
		}
	}

	/** Conveince function to make a new intersection node. */
	public static AstNode makeConjunct(AstNode a, AstNode b) {
		if (a.equals(b)) {
			return a;
		} else {
			return new AstLogic(a, b, Operation.Conjunct);
		}
	}

	/** Conveince function to make a new union node. */
	public static AstNode makeDisjunct(AstNode a, AstNode b) {
		if (a.equals(b)) {
			return a;
		} else {
			return new AstLogic(a, b, Operation.Disjunct);
		}
	};

	/** Conveince function to make a new complement node. */
	public static AstNode makeNegation(AstNode a) {
		if (a instanceof AstLogic) {
			AstLogic n = (AstLogic) a;
			if (n.operation == Operation.Negation) {
				return n.left;
			}
		}
		return new AstLogic(a, null, Operation.Negation);
	}

	/**
	 * Populate a matrix of terms as needed by {@link ComputedInteractome} from
	 * an expression.
	 */
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

	/**
	 * Helper function to create new terms in the matrix when switching form one
	 * type of term to another.
	 */
	private static void prepareMatrix(List<List<Integer>> productOfSums,
			List<List<Integer>> productOfSumsNegated, Operation parent,
			Operation operation) {
		if (parent == Operation.Conjunct && operation != Operation.Conjunct) {
			productOfSums.add(new ArrayList<Integer>());
			productOfSumsNegated.add(new ArrayList<Integer>());
		}
	}

	/** Pushes negation operators to the leaves of the AstLogic tree. */
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

	/**
	 * This function is the meat of converting terms to conjunctive normal form.
	 */
	private AstNode distributeDisjunct() {
		if (operation == Operation.Disjunct) {
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
		} else if (operation == Operation.Conjunct) {
			return makeConjunct(distributeDisjunctOf(left),
					distributeDisjunctOf(right));
		} else {
			return this;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AstLogic) {
			AstLogic logic = (AstLogic) other;
			return operation == logic.operation
					&& left.equals(logic.left)
					&& (right == null ? logic.right == null : right
							.equals(logic.right));
		} else {
			return false;
		}
	}

	@Override
	protected int getNeededParameterCount() {
		return 0;
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

	@Override
	public Type getType() {
		return Type.InteractomeType;
	}

	/**
	 * Populate a matrix of terms as needed by {@link ComputedInteractome} from
	 * an expression.
	 */
	private void makeTermMatrix(List<List<Integer>> productOfSums,
			List<List<Integer>> productOfSumsNegated, List<AstNode> termini,
			Operation parent) {
		prepareMatrix(productOfSums, productOfSumsNegated, parent, operation);
		switch (operation) {
		case Disjunct:
			makeTermMatrixOf(left, productOfSums, productOfSumsNegated,
					termini, operation);
			makeTermMatrixOf(right, productOfSums, productOfSumsNegated,
					termini, operation);
			break;
		case Conjunct:
			makeTermMatrixOf(left, productOfSums, productOfSumsNegated,
					termini, operation);
			makeTermMatrixOf(right, productOfSums, productOfSumsNegated,
					termini, operation);
			break;
		case Negation:
			makeTermMatrixOf(left, productOfSumsNegated, productOfSums,
					termini, operation);
		}
	}

	/**
	 * Collect all the leaves of a AstLogic tree (i.e., AstNode children which
	 * are not AstLogic).
	 */
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

	/** Sorts two parallel lists. */
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
				if (comparison == 0) {
					comparison = value2.compareTo(pivotValue2);
				}
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

	/** Pushes negation operators to the leaves of the AstLogic tree. */
	private AstNode removeNegation() {
		if (operation == Operation.Negation) {
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

	/**
	 * Render this AstLogic tree by rendering all the leaves, creating a
	 * conjunctive normal form matrix of operations, and then calling the
	 * {@link ComputedInteractome} constructor.
	 */
	@Override
	public boolean render(ProgramRoutine program, int depth, int debrujin) {
		AstNode baseNormalForm = distributeDisjunctOf(removeNegation());
		if (baseNormalForm instanceof AstLogic) {
			AstLogic normalForm = (AstLogic) baseNormalForm;

			List<List<Integer>> productOfSums = new ArrayList<List<Integer>>();
			List<List<Integer>> productOfSumsNegated = new ArrayList<List<Integer>>();
			List<AstNode> termini = new ArrayList<AstNode>();
			normalForm.prepareInteractomes(termini);
			Collections.sort(termini, ToStringComparator.instance);
			normalForm.makeTermMatrix(productOfSums, productOfSumsNegated,
					termini, Operation.Conjunct);

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

			try {
				if (!(program.instructions.add(new InstructionPush(
						productOfSumsNegated)) && program.instructions
						.add(new InstructionPush(productOfSums)))) {
					return false;
				}
				for (AstNode node : termini) {
					if (!node.render(program, 0, debrujin)) {
						return false;
					}
				}

				return program.instructions.add(new InstructionPack(termini
						.size()))
						&& program.instructions.add(new InstructionConstruct(
								ComputedInteractome.class.getConstructor(
										List.class, List.class, List.class)));
			} catch (SecurityException e) {
				return false;
			} catch (NoSuchMethodException e) {
				return false;
			}

		} else {
			return baseNormalForm.render(program, 0, debrujin);
		}
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			Environment environment) {
		AstNode left = this.left.resolve(runner, context, environment);
		AstNode right = this.right == null ? null : this.right.resolve(runner,
				context, environment);
		if (left == null || this.right != null && right == null) {
			return null;
		}
		if (this.left == left && this.right == right) {
			return this;
		} else {
			return new AstLogic(left, right, operation);
		}
	}

	public void show(ShowablePrintWriter<AstNode> print) {
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

	/**
	 * Parallel list swap operation for {@link #quicksort(List, List, int, int)}
	 * .
	 */
	private void swap(List<List<Integer>> list1, List<List<Integer>> list2,
			int left, int right) {
		List<Integer> value1 = list1.get(left);
		list1.set(left, list1.get(right));
		list1.set(right, value1);

		List<Integer> value2 = list2.get(left);
		list2.set(left, list2.get(right));
		list2.set(right, value2);
	}

	/** Checks that the arguments are well-typed and of type Interactome. */
	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		if (!(left.type(runner, context) && right.type(runner, context))) {
			return false;
		}

		if (!left.getType().unify(Type.InteractomeType)) {
			runner.appendTypeError(left.getType(), Type.InteractomeType, this,
					context);
			return false;
		}
		if (!right.getType().unify(Type.InteractomeType)) {
			runner.appendTypeError(right.getType(), Type.InteractomeType, this,
					context);
			return false;
		}
		return true;
	}
}
