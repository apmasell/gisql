package ca.wlu.gisql.ast;

import java.util.Set;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** The {@link AstNode} of an unresolved symbol. */
public class AstName extends AstNode {

	private final String name;

	public AstName(String name) {
		super();
		this.name = name;
	}

	@Override
	protected void freeVariables(Set<String> variables) {
		raiseIllegalState();
	}

	public String getName() {
		return name;
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public boolean renderSelf(Rendering program, int depth) {
		return raiseIllegalState();
	}

	@Override
	public void resetType() {
		raiseIllegalState();

	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		AstNode self = environment.lookup(name);
		if (self == null) {
			runner.appendResolutionError("Undefined name", this, context);
			return null;
		}
		return self.resolve(runner, context, environment);
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(name);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return raiseIllegalState();
	}
}
