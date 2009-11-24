package ca.wlu.gisql.ast;

import java.util.Set;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Represents a Java function of some kind that is embedded in the query
 * language.
 */
public abstract class AstNative extends AstNode {

	protected final String description;
	protected final String name;
	protected final Type type;

	protected AstNative(String name, String description, Type... types) {
		super();
		this.name = name;
		this.description = description;
		if (types.length == 1) {
			if (types[0].getArrowDepth() == 0) {
				throw new IllegalArgumentException(
						"Needs to be a function type.");
			}
			type = types[0];
		} else {
			if (types.length < 2) {
				throw new IllegalArgumentException("Need at least two types.");
			}
			for (Type type : types) {
				if (type == null) {
					throw new IllegalArgumentException("Type cannot be null.");
				}
			}
			type = new ArrowType(types);
		}
	}

	@Override
	protected final void freeVariables(Set<String> variables) {
	}

	public String getDescription() {
		return description;
	}

	public final Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public final Type getType() {
		return type;
	}

	@Override
	public final void resetType() {
		/* Do not touch original type. */
	}

	@Override
	public final AstNode resolve(ExpressionRunner runner,
			ExpressionContext context, ResolutionEnvironment environment) {
		return new AstNativeIndirect(this);
	}

	public final void show(ShowablePrintWriter<AstNode> print) {
		print.print(name);
	}

	@Override
	public final boolean type(ExpressionRunner runner, ExpressionContext context) {
		return true;
	}
}
