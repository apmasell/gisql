package ca.wlu.gisql.ast.util;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.runner.ExpressionRunner;

/**
 * Allows an arbitrary Java function to be come part of the query language.
 * Simply subclass this and add to {@link BuiltInResolver}. Derived classes must
 * have exactly one constructor that takes exactly one parameter,
 * {@link ExpressionRunner}. The parameter may be null, but in this case,
 * {@link GenericFunction#run(Object...)} must never be called.
 */
public abstract class Function implements GenericFunction {
	private final String description;
	private final String name;
	protected final ExpressionRunner runner;
	private final Type type;

	public Function(ExpressionRunner runner, String name, String description,
			Type... types) {
		this.runner = runner;
		this.name = name;
		this.description = description;
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

	public final String getDescription() {
		return description;
	}

	public final Type getType() {
		return type;
	}

	@Override
	public final String toString() {
		return name;
	}

}
