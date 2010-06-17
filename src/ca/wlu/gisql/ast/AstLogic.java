package ca.wlu.gisql.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import name.masella.iterator.ArrayIterator;

import org.apache.commons.collections15.iterators.SingletonIterator;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.typeclasses.TypeClass;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.logic.Complement;
import ca.wlu.gisql.interactome.logic.ComputedInteractome;
import ca.wlu.gisql.interactome.logic.Intersection;
import ca.wlu.gisql.interactome.logic.Union;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ToStringComparator;

/**
 * Represents any set operation on {@link Interactome}s. All set operations are
 * broken down into conjunction(intersection), disjunction(union), and negation.
 * All operations are converted into conjunctive normal form.
 */
public class AstLogic extends AstNode {

	private enum Operation {
		Conjunct, Disjunct, Negation
	}

	private enum RawType {
		Bool {
			@Override
			public Class<?> getJavaClass() {
				return Boolean.class;
			}

			@Override
			public <T> boolean render(Rendering<T> program, Operation operation) {
				switch (operation) {
				case Conjunct:
					program.g_Insn(Opcodes.IAND);
					break;
				case Disjunct:
					program.g_Insn(Opcodes.IOR);
					break;
				case Negation:
					program.g_Insn(Opcodes.ICONST_1);
					program.g_Insn(Opcodes.IXOR);
				}
				return true;
			}
		},
		Member {
			@Override
			public Class<?> getJavaClass() {
				return Double.class;
			}

			@Override
			public <T> boolean render(Rendering<T> program, Operation operation) {
				try {
					switch (operation) {
					case Conjunct:
						return program.g_InvokeMethod(Math.class.getMethod(
								"min", double.class, double.class));
					case Disjunct:
						return program.g_InvokeMethod(Math.class.getMethod(
								"max", double.class, double.class));
					case Negation:
						program.g_Insn(Opcodes.DCONST_1);
						program.g_Insn(Opcodes.DSUB);
						program.g_Insn(Opcodes.DNEG);
						return true;
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
				return false;
			}
		};
		abstract Class<?> getJavaClass();

		abstract <T> boolean render(Rendering<T> program, Operation operation);
	}

	/** Convenience function for normalisation. */
	private static AstNode distributeDisjunctOf(AstNode node) {
		if (node instanceof AstLogic) {
			return ((AstLogic) node).distributeDisjunct();
		} else {
			return node;
		}
	}

	/** Convenience function to make a new intersection node. */
	public static AstNode makeConjunct(AstNode a, AstNode b) {
		if (a.equals(b)) {
			return a;
		} else {
			return new AstLogic(a, b, Operation.Conjunct);
		}
	}

	/** Convenience function to make a new union node. */
	public static AstNode makeDisjunct(AstNode a, AstNode b) {
		if (a.equals(b)) {
			return a;
		} else {
			return new AstLogic(a, b, Operation.Disjunct);
		}
	}

	/** Convenience function to make a new complement node. */
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
			List<List<Long>> productOfSums,
			List<List<Long>> productOfSumsNegated, List<AstNode> termini,
			Operation parent) {
		if (node instanceof AstLogic) {
			((AstLogic) node).makeTermMatrix(productOfSums,
					productOfSumsNegated, termini, parent);

		} else {
			prepareMatrix(productOfSums, productOfSumsNegated, parent, null);

			List<Long> term = productOfSums.get(productOfSums.size() - 1);
			long index = termini.indexOf(node);
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
	private static void prepareMatrix(List<List<Long>> productOfSums,
			List<List<Long>> productOfSumsNegated, Operation parent,
			Operation operation) {
		if (parent == Operation.Conjunct && operation != Operation.Conjunct) {
			productOfSums.add(new ArrayList<Long>());
			productOfSumsNegated.add(new ArrayList<Long>());
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

	private final TypeVariable type = new TypeVariable(TypeClass.Logic);

	private AstLogic(AstNode left, AstNode right, Operation operation) {
		this.left = left;
		this.right = right;
		this.operation = operation;
	}

	private AstNode convertList(List<List<Long>> list) {
		AstLiteralList outer = new AstLiteralList();
		for (List<Long> sublist : list) {
			AstLiteralList inner = new AstLiteralList();
			for (Long value : sublist) {
				inner.add(new AstLiteral(Type.NumberType, value));
			}
			outer.add(inner);
		}
		return outer;
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
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		left.freeVariables(variables);
		if (right != null) {
			right.freeVariables(variables);
		}

	}

	public Precedence getPrecedence() {
		switch (operation) {
		case Negation:
			return Complement.descriptor.getPrecedence();
		case Conjunct:
			return Intersection.descriptor.getPrecedence();
		case Disjunct:
			return Union.descriptor.getPrecedence();
		}
		return null;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return left.hashCode() * 17
				+ (right == null ? 0 : right.hashCode() * 19)
				+ operation.hashCode();
	}

	@Override
	public Iterator<AstNode> iterator() {
		return right == null ? new SingletonIterator<AstNode>(left)
				: new ArrayIterator<AstNode>(left, right);
	}

	/**
	 * Populate a matrix of terms as needed by {@link ComputedInteractome} from
	 * an expression.
	 */
	private void makeTermMatrix(List<List<Long>> productOfSums,
			List<List<Long>> productOfSumsNegated, List<AstNode> termini,
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
	private void quicksort(List<List<Long>> productOfSums,
			List<List<Long>> productOfSumsNegated, int left, int right) {
		if (right > left) {
			int pivotIndex = left;
			String pivotValue1 = productOfSums.get(pivotIndex).toString();
			String pivotValue2 = productOfSums.get(pivotIndex).toString();
			swap(productOfSums, productOfSumsNegated, pivotIndex, right);
			int storeIndex = left;
			for (int i = left; i < right; i++) {
				String value1 = productOfSums.get(i).toString();
				String value2 = productOfSumsNegated.get(i).toString();
				int comparison = value1.compareTo(pivotValue1);
				if (comparison == 0) {
					comparison = value2.compareTo(pivotValue2);
				}
				if (comparison < 0) {
					swap(productOfSums, productOfSumsNegated, i, storeIndex);
					storeIndex++;
				}
			}
			swap(productOfSums, productOfSumsNegated, storeIndex, right);
			quicksort(productOfSums, productOfSumsNegated, left, storeIndex - 1);
			quicksort(productOfSums, productOfSumsNegated, storeIndex + 1,
					right);
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
	private <T> boolean renderInteractome(Rendering<T> program,
			AstLogic normalForm) {
		List<List<Long>> productOfSums = new ArrayList<List<Long>>();
		List<List<Long>> productOfSumsNegated = new ArrayList<List<Long>>();
		AstLiteralList termini = new AstLiteralList();
		normalForm.prepareInteractomes(termini);
		Collections.sort(termini, ToStringComparator.instance);
		normalForm.makeTermMatrix(productOfSums, productOfSumsNegated, termini,
				Operation.Conjunct);

		for (int index = 0; index < productOfSums.size(); index++) {
			for (int subindex = index + 1; subindex < productOfSums.size(); subindex++) {
				if (productOfSums.get(index)
						.equals(productOfSums.get(subindex))
						&& productOfSumsNegated.get(index).equals(
								productOfSumsNegated.get(subindex))) {
					productOfSums.remove(subindex);
					productOfSumsNegated.remove(subindex);
				}
			}
			Collections.sort(productOfSums.get(index));
			Collections.sort(productOfSumsNegated.get(index));
		}
		quicksort(productOfSums, productOfSumsNegated, 0,
				productOfSums.size() - 1);

		try {
			return program.hP(convertList(productOfSumsNegated))
					&& program.hP(convertList(productOfSums))
					&& program.hP(termini)
					&& program
							.pRg$hO_CreateObject(ComputedInteractome.class
									.getConstructor(List.class, List.class,
											List.class));
		} catch (SecurityException e) {
			return false;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	/**
	 * Render an logic function of something not made of interactomes and cast
	 * the result to an object.
	 */
	private <T> boolean renderRaw(Rendering<T> program, int depth,
			AstLogic normalForm, RawType rawtype) {
		return normalForm.renderRaw(program, depth, rawtype)
				&& program.pOhO_PrimitiveToObject(rawtype.getJavaClass());
	};

	/**
	 * Render the logic of something that is not interactomes (i.e., booleans or
	 * memberships).
	 */
	private <T> boolean renderRaw(Rendering<T> program, int depth,
			RawType rawtype) {
		if (left instanceof AstLogic) {
			if (!((AstLogic) left).renderRaw(program, depth, rawtype)) {
				return false;
			}
		} else {
			if (!(left.render(program, depth) && program
					.pOhO_ObjectToPrimitive(rawtype.getJavaClass()))) {
				return false;
			}

		}
		if (right == null) {
		} else if (right instanceof AstLogic) {
			if (!((AstLogic) right).renderRaw(program, depth, rawtype)) {
				return false;
			}
		} else {
			if (!(right.render(program, depth) && program
					.pOhO_ObjectToPrimitive(rawtype.getJavaClass()))) {
				return false;
			}
		}
		return rawtype.render(program, operation);
	}

	/**
	 * Render this logical expression. Since logical expressions can be of
	 * multiple types, if the type is known, the correct implementation will be
	 * used. If not, all possible expressions will be generated and a run-time
	 * type check select the right one.
	 */
	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		AstNode baseNormalForm = distributeDisjunctOf(removeNegation());
		if (baseNormalForm instanceof AstLogic) {
			AstLogic normalForm = (AstLogic) baseNormalForm;

			if (!type.isAssigned()) {
				Label bool = new Label();
				Label member = new Label();
				Label end = new Label();

				AstNode terminus = normalForm.left;
				while (terminus instanceof AstLogic) {
					terminus = ((AstLogic) terminus).left;
				}
				return terminus.render(program, depth)
						&& program.g_InstanceOf(RawType.Bool.getJavaClass())
						&& program.jump(Opcodes.IFNE, bool)
						&& program.g_InstanceOf(RawType.Member.getJavaClass())
						&& program.jump(Opcodes.IFNE, member)
						&& program.pO()
						&& renderInteractome(program, normalForm)
						&& program.jump(Opcodes.GOTO, end)
						&& program.mark(bool)
						&& program.pO()
						&& renderRaw(program, depth, normalForm, RawType.Bool)
						&& program.jump(Opcodes.GOTO, end)
						&& program.mark(member)
						&& program.pO()
						&& renderRaw(program, depth, normalForm, RawType.Member)
						&& program.mark(end);
			} else if (type.canUnify(Type.InteractomeType)) {
				return renderInteractome(program, normalForm);
			} else if (type.canUnify(Type.BooleanType)) {
				return renderRaw(program, depth, normalForm, RawType.Bool);
			} else if (type.canUnify(Type.MembershipType)) {
				return renderRaw(program, depth, normalForm, RawType.Member);
			} else {
				throw new IllegalStateException();
			}
		} else {
			return baseNormalForm.render(program, depth);
		}
	}

	@Override
	public void resetType() {
		type.reset();
		left.resetType();
		if (right != null) {
			right.resetType();
		}
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
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
	private void swap(List<List<Long>> productOfSums,
			List<List<Long>> productOfSumsNegated, int left, int right) {
		List<Long> value1 = productOfSums.get(left);
		productOfSums.set(left, productOfSums.get(right));
		productOfSums.set(right, value1);

		List<Long> value2 = productOfSumsNegated.get(left);
		productOfSumsNegated.set(left, productOfSumsNegated.get(right));
		productOfSumsNegated.set(right, value2);
	}

	/** Checks that the arguments are well-typed and of type Interactome. */
	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return runner.typeCheck(left, type, context)
				&& (right == null ? true : runner.typeCheck(right, type,
						context));
	}
}
