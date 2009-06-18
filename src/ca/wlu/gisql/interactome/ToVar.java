package ca.wlu.gisql.interactome;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstInteractome;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class ToVar implements Interactome {
	private static class AstToVar implements AstNode {
		private final Environment environment;
		private final AstNode interactome;
		private final String name;

		private AstToVar(Environment environment, AstNode node, String name) {
			this.environment = environment;
			interactome = node;
			this.name = name;
		}

		public Interactome asInteractome() {
			return new ToVar(environment, interactome.asInteractome(), name);
		}

		public AstNode fork(AstNode substitute) {
			return new AstToVar(environment, interactome.fork(substitute), name);
		}

		public int getPrecedence() {
			return descriptor.getPrecedence();
		}

		public boolean isInteractome() {
			return true;
		}

		public void show(ShowablePrintWriter print) {
			print.print(interactome);
			print.print(" @ ");
			print.print(name);
		}
	}

	public final static Parseable descriptor = new Parseable() {

		public AstNode construct(Environment environment, List<AstNode> params,
				Stack<String> error) {
			AstNode interactome = params.get(0);
			String name = ((AstString) params.get(1)).getString();
			if (name == null) {
				error.push("Missing variable name.");
				return null;
			}
			return new AstToVar(environment, interactome, name);
		}

		public int getPrecedence() {
			return Parser.PREC_ASSIGN;
		}

		public boolean isMatchingOperator(char c) {
			return c == '@';
		}

		public boolean isPrefixed() {
			return false;
		}

		public void show(ShowablePrintWriter print) {
			print.print("Assign to variable: A @ varname");
		}

		public Token[] tasks(Parser parser) {
			return new Token[] { new Name(parser) };
		}

	};

	private final Environment environment;

	private final String name;

	private final Interactome source;

	public ToVar(Environment environment, Interactome source, String name) {
		this.environment = environment;
		this.source = source;
		this.name = name;
	}

	public double calculateMembership(Gene gene) {
		double membership = gene.getMembership(this);
		if (GisQL.isUndefined(membership)) {
			membership = source.calculateMembership(gene);
			gene.setMembership(this, membership);
		}
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = interaction.getMembership(this);
		if (GisQL.isUndefined(membership)) {
			membership = source.calculateMembership(interaction);
			interaction.setMembership(this, membership);
		}
		return membership;
	}

	public int getPrecedence() {
		return descriptor.getPrecedence();
	}

	public Type getType() {
		return source.getType();
	}

	public double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	public boolean postpare() {
		if (!source.postpare())
			return false;
		return environment.setVariable(name, new AstInteractome(this));
	}

	public boolean prepare() {
		return source.prepare();
	}

	public void show(ShowablePrintWriter print) {
		print.print(source, this.getPrecedence());
		print.print(" @ ");
		print.print(name);
	}

	public String toString() {
		return ShowableStringBuilder.toString(this);
	}

}
