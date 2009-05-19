package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.Parser;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.Parseable;

public class Complement implements Interactome {
	public final static Parseable descriptor = new Parseable() {
		public Interactome construct(Environment environment,
				List<Object> params, Stack<String> error) {
			Interactome interactome = (Interactome) params.get(0);
			return new Complement(environment.getTriangularNorm(), interactome);
		}

		public int getNestingLevel() {
			return 5;
		}

		public boolean isMatchingOperator(char c) {
			return c == '!' || c == '¬';
		}

		public boolean isPrefixed() {
			return true;
		}

		public PrintStream show(PrintStream print) {
			print.print("Complement (1-Ax): ¬A, !A");
			return print;
		}

		public StringBuilder show(StringBuilder sb) {
			sb.append("Complement (1-Ax): ¬A, !A");
			return sb;
		}

		public Parser.NextTask[] tasks(Parser parser) {
			return new Parser.NextTask[] { parser.new SubExpression() };
		}

	};

	Interactome interactome;

	private TriangularNorm norm;

	public Complement(TriangularNorm norm, Interactome i) {
		this.interactome = i;
		this.norm = norm;
	}

	public double calculateMembership(Gene gene) {
		return norm.v(interactome.calculateMembership(gene));
	}

	public double calculateMembership(Interaction interaction) {
		return norm.v(interactome.calculateMembership(interaction));
	}

	public Type getType() {
		return Type.Computed;
	}

	public double membershipOfUnknown() {
		return norm.v(0);
	}

	public int numGenomes() {
		return interactome.numGenomes();
	}

	public boolean postpare() {
		return interactome.postpare();
	}

	public boolean prepare() {
		return interactome.prepare();
	}

	public PrintStream show(PrintStream print) {
		print.print("¬(");
		interactome.show(print);
		print.print(")");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("¬(");
		interactome.show(sb);
		sb.append(")");
		return sb;
	}
}
