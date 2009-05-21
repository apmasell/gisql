package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.Parser;
import ca.wlu.gisql.util.Parseable;

public class ToVar extends CachedInteractome {
	public final static Parseable descriptor = new Parseable() {

		public Interactome construct(Environment environment,
				List<Object> params, Stack<String> error) {
			Interactome interactome = (Interactome) params.get(0);
			String name = (String) params.get(1);
			if (name == null) {
				error.push("Missing variable name.");
				return null;
			}
			return new ToVar(environment, interactome, name);
		}

		public int getNestingLevel() {
			return 0;
		}

		public boolean isMatchingOperator(char c) {
			return c == '@';
		}

		public boolean isPrefixed() {
			return false;
		}

		public PrintStream show(PrintStream print) {
			print.print("Assign to variable: A @ varname");
			return null;
		}

		public StringBuilder show(StringBuilder sb) {
			sb.append("Assign to variable: A @ varname");
			return sb;
		}

		public Parser.NextTask[] tasks(Parser parser) {
			return new Parser.NextTask[] { parser.new Name() };
		}

	};

	private final Environment environment;

	private final String name;

	public ToVar(Environment environment, Interactome source, String name) {
		super(source, name, 0, 1);
		this.environment = environment;
		this.name = name;
	}

	public boolean postpare() {
		if (!super.postpare())
			return false;
		return environment.setVariable(name, this);
	}

	public PrintStream show(PrintStream print) {
		source.show(print);
		print.print(" @ ");
		print.print(name);
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		source.show(sb);
		sb.append(" @ ");
		sb.append(name);
		return sb;
	}

}
