package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.interactome.tovar.ToVar;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.vm.InstructionStore;

public class AstToVar extends AstNode {
	private final String name;
	private AstNode node;

	public AstToVar(AstNode node, String name) {
		this.node = node;
		this.name = name;
	}

	@Override
	protected int getNeededParameterCount() {
		return node.getNeededParameterCount();
	}

	public int getPrecedence() {
		return ToVar.descriptor.getPrecedence();
	}

	@Override
	public Type getType() {
		return node.getType();
	}

	@Override
	public boolean render(ProgramRoutine program, int depth, int debrujin) {
		if (!node.render(program, depth, debrujin)) {
			return false;
		}
		return program.instructions.add(new InstructionStore(name, node
				.getType()));
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			Environment environment) {
		AstNode node = this.node.resolve(runner, context, environment);
		if (node == null) {
			return null;
		} else {
			this.node = node;
			return this;
		}
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(node);
		print.print(" @ ");
		print.print(name);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return node.type(runner, context);
	}
}