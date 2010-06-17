package ca.wlu.gisql.ast;

import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.ast.util.Renderable;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.RenderingFunction;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.Prioritizable;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowableStringBuilder;

/**
 * Holds an element after parsing. The {@link Parser} and {@link Parseable}s
 * will construct subclasses of this node. The root node will then be subjected
 * {@link #resolve(ExpressionRunner, ExpressionContext, ResolutionEnvironment)},
 * {@link #type(ExpressionRunner, ExpressionContext)}, {@link #render()} to
 * create a final program. Any node in the parse tree abort the process.
 */
public abstract class AstNode implements Iterable<AstNode>,
		Prioritizable<AstNode, Precedence>, Renderable, Show<AstNode> {

	/**
	 * Find the variables which are not bound in this expression node. For
	 * instance, in <tt>(λ x. <b>f</b> x <b>y</b>)</tt>.
	 */
	public ListOrderedSet<VariableInformation> freeVariables() {
		ListOrderedSet<VariableInformation> variables = new ListOrderedSet<VariableInformation>();
		freeVariables(variables);
		return variables;
	}

	protected abstract void freeVariables(
			ListOrderedSet<VariableInformation> variables);

	/**
	 * Determine the depth of the left-deep application nesting. In
	 * <tt>(f x y z)</tt>, this would be 3, while <tt>(f x (y z))</tt> would be
	 * 2.
	 */
	protected int getLeftDepth() {
		return getType().getArrowDepth();
	}

	/**
	 * This is the {@link Type} of this node and it must return the same object
	 * every time it is called, even if the underlying type changes due to
	 * unification.
	 * 
	 * @throws IllegalStateException
	 *             if this node should been cleaned from the parse tree.
	 */
	public abstract Type getType();

	protected final <T> T raiseIllegalState() {
		throw new IllegalStateException(this.getClass().getName()
				+ " should be cleaned from parse tree.");
	}

	/**
	 * Convert the abstract syntax tree into a real Java class that may be
	 * instantiated.
	 */
	public Class<? extends GenericFunction> render() {
		Rendering<GenericFunction> program = new RenderingFunction(toString(),
				getType(), new Type[0]);
		if (render(program, 0)) {
			return program.generate();
		} else {
			return null;
		}
	}

	/**
	 * A helper function to create closures when necessary during rendering.
	 * This method should be called by any subclasses during rendering.
	 */
	public final <T> boolean render(Rendering<T> program, int depth) {
		int parameters = getLeftDepth() - depth;
		boolean value;

		if (parameters > 0) {
			String command = toString();
			Rendering<GenericFunction> subroutine = new RenderingFunction(
					command, getType(), getType().getParameters());
			ListOrderedSet<VariableInformation> freevars = this.freeVariables();
			value = subroutine.gF$_CreateFields(freevars.asList())
					&& renderSelf(subroutine, depth + parameters)
					&& program.hO_CreateSubroutine(subroutine)
					&& subroutine.gF$_lVhF$_CopyVariablesFromParent(program,
							freevars.asList());
		} else {
			value = renderSelf(program, depth);
		}
		return value;

	}

	/**
	 * Generate VM code based on this node.
	 * 
	 * @param program
	 *            is the current rendered output.
	 * @param depth
	 *            number of parameters on the reference stack in the
	 *            {@link Rendering} that are available to this node.
	 * @return success
	 */
	protected abstract <C> boolean renderSelf(Rendering<C> program, int depth);

	/** Resets the state of the type system so that type checking may be redone. */
	public abstract void resetType();

	/**
	 * Perform any name resolution. A parse tree after resolution should contain
	 * no {@link AstName} nodes. If a node contains errors, it should return
	 * null. Any node with child nodes must return null if the resolved result
	 * of its child is null.
	 * 
	 * @throws IllegalStateException
	 *             if this node should been cleaned from the parse tree.
	 */
	public abstract AstNode resolve(ExpressionRunner runner,
			ExpressionContext context, ResolutionEnvironment environment);

	@Override
	public final String toString() {
		return ShowableStringBuilder.toString(this, null);
	}

	/**
	 * Perform type-checking by doing Hindley-Milner unification. Type checking
	 * should be done on child nodes first, then, a parent should check that the
	 * types of the child nodes agree with the rules for that node.
	 * 
	 * @throws IllegalStateException
	 *             if this node should been cleaned from the parse tree.
	 */
	public abstract boolean type(ExpressionRunner runner,
			ExpressionContext context);

}
