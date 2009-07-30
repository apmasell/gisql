package ca.wlu.gisql.interactome;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.Word;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Orphans implements Interactome {
	static class AstOrphans implements AstNode {

		private final AstNode parameter;

		public AstOrphans(AstNode parameter) {
			this.parameter = parameter;
		}

		public Interactome asInteractome() {
			return new Orphans(parameter.asInteractome());
		}

		public AstNode fork(AstNode substitute) {
			return new AstOrphans(parameter.fork(substitute));
		}

		public int getPrecedence() {
			return descriptor.getPrecedence();
		}

		public boolean isInteractome() {
			return true;
		}

		public void show(ShowablePrintWriter<AstNode> print) {
			print.print(parameter, getPrecedence());
			print.print(" : orphans");
		}

	}

	public static final Parseable descriptor = new Parseable() {

		public AstNode construct(Environment environment, List<AstNode> params,
				Stack<String> error) {
			return new AstOrphans(new Delay.AstDelay(params.get(0)));
		}

		public int getPrecedence() {
			return Parser.PREC_UNARY_MANGLE;
		}

		public boolean isMatchingOperator(char c) {
			return c == ':';
		}

		public boolean isPrefixed() {
			return false;
		}

		public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
			print.print("Find disconnected nodes: A : orphans");
		}

		public Token[] tasks(Parser parser) {
			return new Token[] { new Word(parser, "orphans") };
		}

	};

	private final Interactome source;

	public Orphans(Interactome source) {
		this.source = source;
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		if (GisQL.isPresent(membership)) {
			for (Interaction interaction : gene.getInteractions()) {
				if (GisQL.isPresent(source.calculateMembership(interaction)))
					return GisQL.Missing;
			}
			return membership;
		}
		return GisQL.Missing;
	}

	public double calculateMembership(Interaction interaction) {
		return source.calculateMembership(interaction);
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return source.collectAll(set);
	}

	public int getPrecedence() {
		return descriptor.getPrecedence();
	}

	public Type getType() {
		return Type.Computed;
	}

	public double membershipOfUnknown() {
		return 0;
	}

	public boolean postpare() {
		return source.postpare();
	}

	public boolean prepare() {
		return source.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source, getPrecedence());
		print.print(" : orphans");
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}
}
