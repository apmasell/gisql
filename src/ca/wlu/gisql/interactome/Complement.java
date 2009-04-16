package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.Environment;
import ca.wlu.gisql.Parser;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.gene.RecalculatedGene;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.interaction.RecalculatedInteraction;
import ca.wlu.gisql.util.Parseable;

public class Complement extends AbstractInteractome {
	public final static Parseable descriptor = new Parseable() {
		public Interactome construct(Environment environment,
				List<Object> params, Stack<String> error) {
			Interactome interactome = (Interactome) params.get(0);
			return new Complement(environment.getNorm(), interactome);
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

	public int countOrthologs(Gene gene) {
		return interactome.countOrthologs(gene);
	}

	public Gene findOrtholog(Gene gene) {
		return interactome.findOrtholog(gene);
	}

	protected double membershipOfUnknown() {
		return norm.v(0);
	}

	public int numGenomes() {
		return interactome.numGenomes();
	}

	protected void prepareInteractions() {
		for (Gene gene : interactome.genes()) {
			addGene(new RecalculatedGene(gene, norm.v(gene.getMembership())));
		}

		for (Interaction interaction : interactome) {
			this.addInteraction(new RecalculatedInteraction(interaction, norm
					.v(interaction.getMembership())));
		}
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
