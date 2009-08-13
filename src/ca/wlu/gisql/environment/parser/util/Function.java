package ca.wlu.gisql.environment.parser.util;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowablePrintWriter;

public abstract class Function implements Parseable {
	public static class Expression extends Parameter {
		Token createTask() {
			return ca.wlu.gisql.environment.parser.Expression.self;
		}

		public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
			print.print("expression");
		}
	}

	public static class ListExpression extends Parameter {
		private final String description;

		public ListExpression(String description) {
			this.description = description;
		}

		Token createTask() {
			return ca.wlu.gisql.environment.parser.ListExpression.instance;
		}

		public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
			print.print(description);
		}

	}

	public static class Name extends Parameter {
		private final String description;

		public Name(String description) {
			this.description = description;
		}

		Token createTask() {
			return new ca.wlu.gisql.environment.parser.Name();
		}

		public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
			print.print(description);
		}

	}

	public static abstract class Parameter implements Show<ParserKnowledgebase> {
		abstract Token createTask();
	}

	public static class QuotedString extends Parameter {
		private final String description;

		public QuotedString(String description) {
			this.description = description;
		}

		Token createTask() {
			return ca.wlu.gisql.environment.parser.QuotedString.self;
		}

		public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
			print.print("\"");
			print.print(description);
			print.print("\"");
		}

	}

	private static final Token TOKEN_CLOSEPAREN = ca.wlu.gisql.environment.parser.Literal
			.get(')');
	private static final Token TOKEN_COMMA = ca.wlu.gisql.environment.parser.Literal
			.get(',');
	private static final Token TOKEN_OPENPAREN = ca.wlu.gisql.environment.parser.Literal
			.get('(');
	private final Parameter[] parameters;

	private final Token[] tasks;
	private final String word;

	protected Function(String word, Parameter[] parameters) {
		this.word = word;
		this.parameters = parameters;
		tasks = new Token[3 + (parameters == null ? 0
				: 2 * parameters.length - 1)];
		int index = 0;
		tasks[index++] = new ca.wlu.gisql.environment.parser.Word(word);
		tasks[index++] = TOKEN_OPENPAREN;
		if (parameters != null) {
			boolean first = true;
			for (Parameter parameter : parameters) {
				if (first) {
					first = false;
				} else {
					tasks[index++] = TOKEN_COMMA;
				}
				tasks[index++] = parameter.createTask();
			}
		}
		tasks[index++] = TOKEN_CLOSEPAREN;

	}

	public int getPrecedence() {
		return Parser.PREC_FUNCTION;
	}

	public boolean isMatchingOperator(char c) {
		return c == ':';
	}

	public boolean isPrefixed() {
		return true;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
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
				print.print(parameter);
			}
		}
		print.print(")");
	}

	public Token[] tasks() {
		return tasks;
	}
}
