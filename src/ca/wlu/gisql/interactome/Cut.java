package ca.wlu.gisql.interactome;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Decimal;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstDouble;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Cut implements Interactome {
	private static class AstCut implements AstNode {
		private double cutoff;
		private AstNode parameter;

		public AstCut(AstNode node, double cutoff) {
			parameter = node;
			this.cutoff = cutoff;
		}

		public Interactome asInteractome() {
			return new Cut(parameter.asInteractome(), cutoff);
		}

		public AstNode fork(AstNode substitute) {
			return new AstCut(parameter.fork(substitute), cutoff);
		}

		public int getPrecedence() {
			return descriptor.getPrecedence();
		}

		public boolean isInteractome() {
			return true;
		}

		public void show(ShowablePrintWriter print) {
			print.print(parameter, getPrecedence());
			print.print(" [");
			print.print(cutoff);
			print.print("]");
		}

	}

	public final static Parseable descriptor = new Parseable() {

		public AstNode construct(Environment environment, List<AstNode> params,
				Stack<String> error) {
			AstNode interactome = params.get(0);
			double cutoff = ((AstDouble) params.get(1)).getDouble();
			if (cutoff > 1.0 || cutoff < 0 || !interactome.isInteractome())
				return null;
			return new AstCut(interactome, cutoff);
		}

		public int getPrecedence() {
			return 0;
		}

		public boolean isMatchingOperator(char c) {
			return c == '[';
		}

		public boolean isPrefixed() {
			return false;
		}

		public void show(ShowablePrintWriter print) {
			print.print("Cut-off (Ax|x>c): A [c]");
		}

		public Token[] tasks(Parser parser) {
			return new Token[] { new Decimal(parser), new Literal(parser, ']') };
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

	public int getPrecedence() {
		return descriptor.getPrecedence();
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

	public void show(ShowablePrintWriter print) {
		print.print(interactome, this.getPrecedence());
		print.print(" [");
		print.print(cutoff);
		print.print("]");

	}

	public String toString() {
		return ShowableStringBuilder.toString(this);
	}
}
