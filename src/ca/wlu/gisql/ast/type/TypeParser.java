package ca.wlu.gisql.ast.type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

import ca.wlu.gisql.annotation.GisqlType;
import ca.wlu.gisql.ast.AstNativeConstructor;

/**
 * Converts a string into a query language type object. Used by
 * {@link AstNativeConstructor} to process {@link GisqlType} annotations. This
 * class is not capable of parsing all possible types valid in the system. Any
 * type present as a static member of {@link Type} will be processed
 * automatically.
 */
public class TypeParser {
	private static final Logger log = Logger.getLogger(TypeParser.class);

	private final String expression;

	private int position = 0;

	public TypeParser(String expression) {
		super();
		this.expression = expression;
	}

	public Type parse() {
		return parse(null);
	}

	private Type parse(Character endofinput) {
		Type type = null;

		while (position < expression.length()) {
			char codepoint = expression.charAt(position);
			if (endofinput != null && codepoint == endofinput) {
				position++;
				return type;
			} else if (Character.isWhitespace(codepoint)) {
				position++;
			} else if (Character.isJavaIdentifierStart(codepoint)) {
				StringBuilder sb = new StringBuilder();
				sb.append(codepoint);
				position++;

				while (position < expression.length()
						&& Character.isJavaIdentifierPart(expression
								.charAt(position))) {
					sb.append(expression.charAt(position));
					position++;
				}
				String typename = sb.toString();
				for (Field field : Type.class.getFields()) {
					if (Modifier.isStatic(field.getModifiers())
							&& Type.class.isAssignableFrom(field.getType())) {
						Type matchtype;
						try {
							matchtype = (Type) field.get(null);
						} catch (IllegalArgumentException e) {
							log.error("Failed to access field.", e);
							return null;
						} catch (IllegalAccessException e) {
							log.error("Failed to access field.", e);
							return null;
						}
						if (matchtype.toString().equals(typename)) {
							type = matchtype;
							break;
						}
					}
				}
				if (type == null) {
					return null;
				}
			} else if (codepoint == '→') {
				position++;
				Type right = parse(endofinput);
				if (right == null) {
					return null;
				}
				return new ArrowType(type, right);
			} else if (codepoint == '[') {
				position++;
				Type inner = parse(']');
				if (inner == null) {
					return null;
				}
				type = new ListType(inner);
			} else if (codepoint == '(') {
				position++;
				Type inner = parse(')');
				if (inner == null) {
					return null;
				}
				type = inner;
			} else {
				return null;
			}
		}

		if (endofinput == null) {
			return type;
		} else {
			return null;
		}
	}
}
