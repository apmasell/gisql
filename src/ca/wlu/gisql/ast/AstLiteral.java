package ca.wlu.gisql.ast;

import java.util.Set;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * An {@link AstNode} that represents a literal value of some particular type.
 * The value will be checked against the supplied type.
 */
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
	protected void freeVariables(Set<String> variables) {
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return type;
	}

	/**
	 * This kind of constant must be representable as a Java constant.
	 * Effectively, any primitive type or String. Object types cannot be in the
	 * constant pool.
	 */
	@Override
	public boolean renderSelf(Rendering program, int depth) {
		return program.hO_AsObject(value);
	}

	@Override
	public void resetType() {
		if (type instanceof TypeVariable) {
			((TypeVariable) type).reset();
		}
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
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
