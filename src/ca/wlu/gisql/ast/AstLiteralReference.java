package ca.wlu.gisql.ast;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * An {@link AstNode} that represents a literal value of some particular type.
 */
public class AstLiteralReference extends AstNode {
	private static final Logger log = Logger
			.getLogger(AstLiteralReference.class);
	private final Field field;

	private final Type type;

	public AstLiteralReference(Field field, Type type) {
		super();
		this.field = field;
		this.type = type;
		if (!Modifier.isStatic(field.getModifiers())
				|| !Modifier.isPublic(field.getModifiers())) {
			throw new IllegalArgumentException(
					"References to literals must access public static members.");
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
	 * This object cannot be represented as a primitive type. A reference to a
	 * static field allows the generated class to find the constant at run time.
	 */
	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		return program.lFhO(field);
	}

	@Override
	public void resetType() {
		type.reset();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		return this;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		boolean isString = type.canUnify(Type.StringType);
		if (isString) {
			print.print('"');
		}
		try {
			print.print(field.get(null));
		} catch (IllegalArgumentException e) {
			log.error("Could not access field.", e);
		} catch (IllegalAccessException e) {
			log.error("Could not access field.", e);
		}
		if (isString) {
			print.print('"');
		}
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return true;
	}
}
