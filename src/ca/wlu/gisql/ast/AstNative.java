package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Represents a Java function of some kind that is embedded in the query
 * language. 
 */
public abstract class AstNative extends AstNode {

	private final String description;
	protected final String name;
	protected final Type[] types;

	protected AstNative(String name, String description, Type... types) {
		super();
		if (types.length < 2) {
			throw new IllegalArgumentException("Need at least two types.");
		}
		for (Type type : types) {
			if (type == null) {
				throw new IllegalArgumentException("Type cannot be null.");
			}
		}
		this.name = name;
		this.types = types;
		this.description = description;
	}

	public final int getArgumentCount() {
		return types.length - 1;
	}

	public final String getDescription() {
		return description;
	}

	public final String getName() {
		return name;
	}

	/**
	 * The number of parameters needed by this node is the number of arguments
	 * that it expects.
	 */
	@Override
	protected final int getNeededParameterCount() {
		return types.length - 1;
	}

	public final int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	@Override
	public Type getType() {
		Type type = types[types.length - 1];
		for (int i = types.length - 2; i >= 0; i--) {
			type = new ArrowType(types[i], type);
			if (types[i] instanceof TypeVariable) {
			}
		}
		return type;
	}

	@Override
	public final AstNode resolve(ExpressionRunner runner,
			ExpressionContext context, Environment environment) {
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
