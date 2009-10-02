package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.vm.InstructionVariable;

public class AstParameter extends AstNode {

	int debrujin = -1;

	final String name;

	final Type type = new TypeVariable();

	public AstParameter(String name) {
		this.name = name;
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
		return program.instructions.add(new InstructionVariable(debrujin
				- this.debrujin));
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			Environment environment) {
		throw new IllegalStateException(
				"Lambda parameters should not exist yet.");
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(name);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return true;
	}

}