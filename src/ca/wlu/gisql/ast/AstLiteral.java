package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.vm.InstructionPush;

public class AstLiteral extends AstNode {
	private final Type type;
	private final Object value;

	public AstLiteral(Type type, Object value) {
		super();
		if (type.validate(value)) {
			this.type = type;
			this.value = value;
		} else {
			throw new IllegalArgumentException(
					"Value provided does not match type provided.");
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AstLiteral) {
			return value == ((AstLiteral) other).value;
		} else {
			return false;
		}
	}

	@Override
	protected int getNeededParameterCount() {
		return 0;
	}

	public int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public boolean render(ProgramRoutine program, int depth, int debrujin) {
		return program.instructions.add(new InstructionPush(value));
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			Environment environment) {
		return this;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		boolean isString = value instanceof String;
		if (isString) {
			print.print('"');
		}
		print.print(value);
		if (isString) {
			print.print('"');
		}
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return true;
	}
}
