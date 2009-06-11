package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Decimal;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.NextTask;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;

public class Cut implements Interactome {
	public final static Parseable descriptor = new Parseable() {

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
			return new NextTask[] { new Decimal(parser),
					new Literal(parser, ']') };
		}

	};

	private final double cutoff;

	private final Interactome interactome;

	public Cut(Interactome interactome, double cutoff) {
		super();
		this.interactome = interactome;
		this.cutoff = cutoff;
	}

	public double calculateMembership(Gene gene) {
		double membership = interactome.calculateMembership(gene);
		if (GisQL.isMissing(membership) || membership < cutoff)
			return GisQL.Missing;
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = interactome.calculateMembership(interaction);
		if (GisQL.isMissing(membership) || membership < cutoff)
			return GisQL.Missing;
		return membership;
	}

	public Interactome fork(Interactome substitute) {
		return new Cut(interactome.fork(substitute), cutoff);
	}

	public int getPrecedence() {
		return descriptor.getNestingLevel();
	}

	public Type getType() {
		return Type.Computed;
	}

	public double membershipOfUnknown() {
		return interactome.membershipOfUnknown();
	}

	public boolean needsFork() {
		return interactome.needsFork();
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
		InteractomeUtil
				.precedenceShow(print, interactome, this.getPrecedence());
		print.print(" [");
		print.print(cutoff);
		print.print("]");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		InteractomeUtil.precedenceShow(sb, interactome, this.getPrecedence());
		sb.append(" [");
		sb.append(cutoff);
		sb.append("]");
		return sb;
	}
}
