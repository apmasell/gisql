package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Prioritizable;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowableStringBuilder;
import ca.wlu.gisql.vm.Instruction;
import ca.wlu.gisql.vm.InstructionPush;

public abstract class AstNode implements Prioritizable<AstNode>, Show<AstNode> {

	/* Normal parsing process is resolve, type, render. */

	protected abstract int getNeededParameterCount();

	public abstract Type getType();

	public abstract boolean render(ProgramRoutine program, int depth,
			int debrujin);

	public abstract AstNode resolve(ExpressionRunner runner,
			ExpressionContext context, Environment environment);

	@Override
	public final String toString() {
		return ShowableStringBuilder.toString(this, null);
	}

	public abstract boolean type(ExpressionRunner runner,
			ExpressionContext context);

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
