package ca.wlu.gisql.environment.parser.util;

import java.io.PrintStream;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.util.Show;

public abstract class Function implements Parseable {
	public static class Expression extends Parameter {
		Token createTask(Parser parser) {
			return new ca.wlu.gisql.environment.parser.Expression(parser);
		}

		public PrintStream show(PrintStream print) {
			print.print("expression");
			return print;
		}

		public StringBuilder show(StringBuilder sb) {
			sb.append("expression");
			return sb;
		}
	}

	public static class Name extends Parameter {
		private final String description;

		public Name(String description) {
			this.description = description;
		}

		Token createTask(Parser parser) {
			return new ca.wlu.gisql.environment.parser.Name(parser);
		}

		public PrintStream show(PrintStream print) {
			print.print(description);
			return print;
		}

		public StringBuilder show(StringBuilder sb) {
			sb.append(description);
			return sb;
		}

	}

	public static abstract class Parameter implements Show {
		abstract Token createTask(Parser parser);
	}

	public static class QuotedString extends Parameter {
		private String description;

		public QuotedString(String description) {
			this.description = description;
		}

		Token createTask(Parser parser) {
			return new ca.wlu.gisql.environment.parser.QuotedString(parser);
		}

		public PrintStream show(PrintStream print) {
			print.print("\"");
			print.print(description);
			print.print("\"");
			return print;
		}

		public StringBuilder show(StringBuilder sb) {
			sb.append("\"");
			sb.append(description);
			sb.append("\"");
			return sb;
		}

	}

	private final Parameter[] parameters;

	private final String word;

	protected Function(String word, Parameter[] parameters) {
		this.word = word;
		this.parameters = parameters;
	}

	public int getNestingLevel() {
		return 0;
	}

	public boolean isMatchingOperator(char c) {
		return c == ':';
	}

	public boolean isPrefixed() {
		return true;
	}

	public PrintStream show(PrintStream print) {
		print.print(":");
		print.print(word);
		print.print("(");
		if (parameters != null) {
			boolean first = true;
			for (Parameter parameter : parameters) {
				if (first) {
					first = false;
				} else {
					print.print(", ");
				}
				parameter.show(print);
			}
		}
		print.print(")");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(":");
		sb.append(word);
		sb.append("(");
		if (parameters != null) {
			boolean first = true;
			for (Parameter parameter : parameters) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				parameter.show(sb);
			}
		}
		sb.append(")");
		return sb;
	}

	public Token[] tasks(Parser parser) {
		Token[] tasks = new Token[3 + (parameters == null ? 0
				: 2 * parameters.length - 1)];
		int index = 0;
		tasks[index++] = new ca.wlu.gisql.environment.parser.Word(parser, word);
		tasks[index++] = new ca.wlu.gisql.environment.parser.Literal(parser,
				'(');
		if (parameters != null) {
			boolean first = true;
			for (Parameter parameter : parameters) {
				if (first) {
					first = false;
				} else {
					tasks[index++] = new ca.wlu.gisql.environment.parser.Literal(
							parser, ',');
				}
				tasks[index++] = parameter.createTask(parser);
			}
		}
		tasks[index++] = new ca.wlu.gisql.environment.parser.Literal(parser,
				')');

		return tasks;
	}
}
