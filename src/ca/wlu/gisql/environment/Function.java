package ca.wlu.gisql.environment;

import java.io.PrintStream;

import ca.wlu.gisql.environment.Parser.NextTask;
import ca.wlu.gisql.util.Parseable;
import ca.wlu.gisql.util.Show;

abstract class Function implements Parseable {
	static class Expression extends Parameter {
		NextTask createTask(Parser parser) {
			return parser.new Expression();
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

	static class Name extends Parameter {
		private final String description;

		Name(String description) {
			this.description = description;
		}

		NextTask createTask(Parser parser) {
			return parser.new Name();
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

	static abstract class Parameter implements Show {
		abstract NextTask createTask(Parser parser);
	}

	static class QuotedString extends Parameter {
		private String description;

		QuotedString(String description) {
			this.description = description;
		}

		NextTask createTask(Parser parser) {
			return parser.new QuotedString();
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

	public NextTask[] tasks(Parser parser) {
		NextTask[] tasks = new NextTask[3 + (parameters == null ? 0
				: 2 * parameters.length - 1)];
		int index = 0;
		tasks[index++] = parser.new Word(word);
		tasks[index++] = parser.new Literal('(');
		if (parameters != null) {
			boolean first = true;
			for (Parameter parameter : parameters) {
				if (first) {
					first = false;
				} else {
					tasks[index++] = parser.new Literal(',');
				}
				tasks[index++] = parameter.createTask(parser);
			}
		}
		tasks[index++] = parser.new Literal(')');

		return tasks;
	}
}
