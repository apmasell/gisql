package ca.wlu.gisql.ast;

import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
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
		type = self.getType().fresh();
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		self.freeVariables(variables);
	}

	@Override
	protected int getLeftDepth() {
		return self.getLeftDepth();
	}

	public final Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		return self.render(program, depth);
	}

	@Override
	public void resetType() {
		type = self.getType().fresh();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
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
