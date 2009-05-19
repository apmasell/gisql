package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.Parser;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.Parseable;

public class ToVar implements Interactome {
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
			return new Parser.NextTask[] { parser.new Literal('$'),
					parser.new Name() };
		}

	};

	private Environment environment;

	private String name;

	private Interactome source;

	private NamedInteractome target;

	public ToVar(Environment environment, Interactome source, String name) {
		super();
		this.environment = environment;
		this.target = new NamedInteractome("$" + name, source.numGenomes(),
				source.membershipOfUnknown(), Type.Computed);
		this.source = source;
		this.name = name;
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		gene.setMembership(target, membership);
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		interaction.setMembership(target, membership);
		return membership;
	}

	public Type getType() {
		return source.getType();
	}

	public double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	public int numGenomes() {
		return source.numGenomes();
	}

	public boolean postpare() {
		environment.setVariable(name, target);
		return true;
	}

	public boolean prepare() {
		return source.prepare();
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
