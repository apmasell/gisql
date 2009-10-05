package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.vm.InstructionPush;

public class AstTypeOf extends AstNode {
	private final AstNode parameter;

	public AstTypeOf(AstNode parameter) {
		super();
		this.parameter = parameter;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AstTypeOf) {
			return parameter.equals(((AstTypeOf) other).parameter);
		} else {
			return false;
		}
	}

	@Override
	protected int getNeededParameterCount() {
		return 0;
	}

	public int getPrecedence() {
		return Parser.PREC_FUNCTION;
	}

	@Override
	public Type getType() {
		return Type.StringType;
	}

	@Override
	public boolean render(ProgramRoutine program, int depth, int debrujin) {
		return program.instructions.add(new InstructionPush(parameter.getType()
				.toString()));
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			Environment environment) {
		AstNode parameter = this.parameter
				.resolve(runner, context, environment);
		if (parameter == null) {
			return null;
		} else {
			return new AstTypeOf(parameter);
		}
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("# ");
		print.print(parameter);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return parameter.type(runner, context);
	}
}