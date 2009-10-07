package ca.wlu.gisql.ast.type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TypeParser {
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
			} else if (Character.isJavaIdentifierPart(codepoint)) {
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
							&& field.getType() == Type.class) {
						Type matchtype;
						try {
							matchtype = (Type) field.get(null);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
							return null;
						} catch (IllegalAccessException e) {
							e.printStackTrace();
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
