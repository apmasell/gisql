package ca.wlu.gisql.ast.type;

import java.util.Vector;

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
	private final String expression;

	private int position = 0;

	private final Vector<Type> variables = new Vector<Type>();

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
			} else if (codepoint >= 'α' && codepoint <= 'ω') {
				position++;
				int index = codepoint - 'α';
				if (index < variables.size()) {
					type = variables.get(index);
				} else {
					variables.setSize(index + 1);
				}
				if (type == null) {
					type = new TypeVariable();
					variables.set(index, type);
				}
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
				type = Type.getTypeForName(sb.toString());
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
