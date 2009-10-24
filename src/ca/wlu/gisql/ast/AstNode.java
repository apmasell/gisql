package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Prioritizable;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowableStringBuilder;
import ca.wlu.gisql.vm.Instruction;
import ca.wlu.gisql.vm.InstructionPush;

/**
 * Holds an element after parsing. The {@link Parser} and {@link Parseable}s
 * will construct subclasses of this node. The root node will then be subjected
 * {@link #resolve(ExpressionRunner, ExpressionContext, Environment)},
 * {@link #type(ExpressionRunner, ExpressionContext)},
 * {@link #render(ProgramRoutine, int, int)} to create a final proram. Any node
 * in the parse tree abort the process.
 */
public abstract class AstNode implements Prioritizable<AstNode>, Show<AstNode> {

	protected abstract int getNeededParameterCount();

	/**
	 * This is the {@link Type} of this node and it must return the same object
	 * every time it is called, even if the underlying type changes due to
	 * unification.
	 * 
	 * @throws IllegalStateException
	 *             if this node should been cleaned from the parse tree.
	 */
	public abstract Type getType();

	/**
	 * Generate VM code based on this node.
	 * 
	 * @param depth
	 *            is the number of left-associated application nodes seen so
	 *            far.
	 * @param debrujin
	 *            is the number of variables on the variable stack.
	 */
	public abstract boolean render(ProgramRoutine program, int depth,
			int debrujin);

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
			ExpressionContext context, Environment environment);

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

	/** A helper function to create closures when necessary. */
	protected final boolean wrap(ProgramRoutine program, int depth, int debrujin) {
		int parameters = getNeededParameterCount();
		String command = toString();
		ProgramRoutine subroutine = new ProgramRoutine(command);

		if (render(subroutine, parameters, debrujin)
				&& subroutine.instructions.add(Instruction.Return)) {
			for (; parameters > 1; parameters--) {
				ProgramRoutine outerroutine = new ProgramRoutine(command);
				if (!(subroutine.instructions.add(Instruction.Return)
						&& outerroutine.instructions.add(new InstructionPush(
								subroutine)) && outerroutine.instructions
						.add(Instruction.Close))) {
					return false;
				}
				subroutine = outerroutine;
			}
			return subroutine.instructions.add(Instruction.Return)
					&& program.instructions
							.add(new InstructionPush(subroutine));
		} else {
			return false;
		}

	}

}
