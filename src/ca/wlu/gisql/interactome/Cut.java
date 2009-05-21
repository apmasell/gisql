package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.Parser;
import ca.wlu.gisql.environment.Parser.NextTask;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.Parseable;

public class Cut implements Interactome {
	public static Parseable descriptor = new Parseable() {

		public Interactome construct(Environment environment,
				List<Object> params, Stack<String> error) {
			Interactome interactome = (Interactome) params.get(0);
			double cutoff = (Double) params.get(1);
			if (cutoff > 1.0 || cutoff < 0)
				return null;
			return new Cut(interactome, cutoff);
		}

		public int getNestingLevel() {
			return 0;
		}

		public boolean isMatchingOperator(char c) {
			return c == '[';
		}

		public boolean isPrefixed() {
			return false;
		}

		public PrintStream show(PrintStream print) {
			print.print("Cut-off (Ax|x>c): A [c]");
			return print;
		}

		public StringBuilder show(StringBuilder sb) {
			sb.append("Cut-off (Ax|x>c): A [c]");
			return sb;
		}

		public NextTask[] tasks(Parser parser) {
			return new NextTask[] { parser.new Decimal(),
					parser.new Literal(']') };
		}

	};

	private double cutoff;

	private Interactome interactome;

	public Cut(Interactome interactome, double cutoff) {
		super();
		this.interactome = interactome;
		this.cutoff = cutoff;
	}

	public double calculateMembership(Gene gene) {
		double membership = interactome.calculateMembership(gene);
		if (Double.isNaN(membership) || membership < cutoff)
			return Double.NaN;
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = interactome.calculateMembership(interaction);
		if (Double.isNaN(membership) || membership < cutoff)
			return Double.NaN;
		return membership;
	}

	public Type getType() {
		return Type.Computed;
	}

	public double membershipOfUnknown() {
		return interactome.membershipOfUnknown();
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
		interactome.show(print);
		print.print(" [");
		print.print(cutoff);
		print.print("]");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		interactome.show(sb);
		sb.append(" [");
		sb.append(cutoff);
		sb.append("]");
		return sb;
	}

}
