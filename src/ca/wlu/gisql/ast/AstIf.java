package ca.wlu.gisql.ast;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import ca.wlu.gisql.ast.type.OptionalMaybeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstIf extends AstNode {
	private final AstNode condition;
	private final AstNode falsepart;
	private final AstNode truepart;
	private final Type type = new OptionalMaybeType(new TypeVariable());

	public AstIf(AstNode condition, AstNode truepart, AstNode falsepart) {
		this.condition = condition;
		this.truepart = truepart;
		this.falsepart = falsepart;
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		condition.freeVariables(variables);
		truepart.freeVariables(variables);
		falsepart.freeVariables(variables);
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		Label elsepart = new Label();
		Label end = new Label();
		return condition.render(program, depth)
				&& program.pOhO_ObjectToPrimitive(Boolean.class)
				&& program.jump(Opcodes.IFEQ, elsepart)
				&& truepart.render(program, depth)
				&& program.jump(Opcodes.GOTO, end) && program.mark(elsepart)
				&& falsepart.render(program, depth) && program.mark(end);
	}

	@Override
	public void resetType() {
		condition.resetType();
		truepart.resetType();
		falsepart.resetType();
		type.reset();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		AstNode condition = this.condition
				.resolve(runner, context, environment);
		AstNode truepart = this.truepart.resolve(runner, context, environment);
		AstNode falsepart = this.falsepart
				.resolve(runner, context, environment);

		if (condition == this.condition && truepart == this.truepart
				&& falsepart == this.falsepart) {
			return this;
		} else {
			return new AstIf(condition, truepart, falsepart);
		}
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("if ");
		print.print(condition, getPrecedence());
		print.print(" then ");
		print.print(truepart, getPrecedence());
		print.print(" else ");
		print.print(falsepart, getPrecedence());
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return runner.typeCheck(condition, Type.BooleanType, context)
				&& runner.typeCheck(truepart, type, context)
				&& runner.typeCheck(falsepart, type, context);
	}

}
