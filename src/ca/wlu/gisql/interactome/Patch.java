package ca.wlu.gisql.interactome;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Decimal;
import ca.wlu.gisql.environment.parser.Maybe;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstDouble;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Patch implements Interactome {
	private static class AstPatch implements AstNode {
		private final Double membership;
		private final AstNode parameter;

		private AstPatch(AstNode parameter, Double membership) {
			this.parameter = parameter;
			this.membership = membership;
		}

		public Interactome asInteractome() {
			return new Patch(parameter.asInteractome(), membership);
		}

		public AstNode fork(AstNode substitute) {
			return new AstPatch(parameter.fork(substitute), membership);
		}

		public int getPrecedence() {
			return descriptor.getPrecedence();
		}

		public boolean isInteractome() {
			return true;
		}

		public void show(ShowablePrintWriter<AstNode> print) {
			print.print(parameter, descriptor.getPrecedence());
			print.print(" $");
			if (membership != null) {
				print.print(" ");
				print.print(membership);
			}
		}
	}

	public final static Parseable descriptor = new Parseable() {

		public AstNode construct(Environment environment, List<AstNode> params,
				Stack<String> error) {
			AstNode interactome = params.get(0);
			AstDouble membership = (AstDouble) params.get(1);
			if (interactome.isInteractome()) {
				if (membership != null
						&& (membership.getDouble() > 1.0 || membership
								.getDouble() < 0.0)) {
					return null;
				}
				return new AstPatch(interactome, (membership == null ? null
						: membership.getDouble()));
			} else {
				return null;
			}
		}

		public int getPrecedence() {
			return Parser.PREC_UNARY_MANGLE;
		}

		public boolean isMatchingOperator(char c) {
			return c == '$';
		}

		public boolean isPrefixed() {
			return false;
		}

		public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
			print.print("Fill-in-the-blanks: A $ [value]");
		}

		public Token[] tasks(Parser parser) {
			return new Token[] { new Maybe(parser, new Decimal(parser)) };
		}

	};

	private final Double membership;
	private final Interactome source;

	public Patch(Interactome source, Double membership) {
		this.source = source;
		this.membership = membership;
	}

	public double calculateMembership(Gene gene) {
		return source.calculateMembership(gene);
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (GisQL.isMissing(membership)
				&& !GisQL.isMissing(source.calculateMembership(interaction
						.getGene1()))
				&& !GisQL.isMissing(source.calculateMembership(interaction
						.getGene2()))) {
			if (this.membership == null) {
				return interaction.getAverageMembership();
			} else {
				return this.membership;
			}
		} else {
			return membership;
		}
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
		return source.membershipOfUnknown();
	}

	public boolean postpare() {
		return source.postpare();
	}

	public boolean prepare() {
		return source.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source, this.getPrecedence());
		print.print(" $");
		if (membership != null) {
			print.print(" ");
			print.print(membership);
		}
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}
}
