package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Wraps an {@link AstNative} node to make copies of the type so that the
 * original generic type is not polluted.
 */
public class AstNativeIndirect extends AstNode {

	private final AstNative self;
	private Type type;

	AstNativeIndirect(AstNative self) {
		this.self = self;
		type = self.getType().clone();
	}

	@Override
	protected int getNeededParameterCount() {
		return self.getNeededParameterCount();
	}

	public final Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public boolean render(ProgramRoutine program, int depth, int debrujin) {
		return self.render(program, depth, debrujin);
	}

	@Override
	public void resetType() {
		type = self.getType().clone();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			Environment environment) {
		return self.resolve(runner, context, environment);
	}

	public final void show(ShowablePrintWriter<AstNode> print) {
		print.print(self);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return true;
	}

}
