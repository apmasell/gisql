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
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class Level implements Interactome {
	static class AstLevel implements AstNode {
		private final AstNode parameter;

		public AstLevel(AstNode parameter) {
			this.parameter = parameter;
		}

		public Interactome asInteractome() {
			return new Level(parameter.asInteractome());
		}

		public AstNode fork(AstNode substitute) {
			return new AstLevel(parameter.fork(substitute));
		}

		public int getPrecedence() {
			return descriptor.getPrecedence();
		}

		public boolean isInteractome() {
			return true;
		}

		public void show(ShowablePrintWriter<AstNode> print) {
			print.print(parameter, getPrecedence());
			print.print(" =");
		}

	}

	public static Parseable descriptor = new Parseable() {

		public AstNode construct(Environment environment, List<AstNode> params,
				Stack<String> error) {
			AstNode interactome = params.get(0);
			if (interactome.isInteractome()) {
				return new AstLevel(interactome);
			} else {
				return null;
			}
		}

		public int getPrecedence() {
			return Parser.PREC_UNARY_MANGLE;
		}

		public boolean isMatchingOperator(char c) {
			return c == '=';
		}

		public boolean isPrefixed() {
			return false;
		}

		public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
			print.print("Defuzzify memberships: A =");
		}

		public Token[] tasks(Parser parser) {
			return null;
		}

	};

	private final Interactome source;

	public Level(Interactome interactome) {
		source = interactome;
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		if (GisQL.isPresent(membership)) {
			return 1;
		} else {
			return GisQL.Missing;
		}
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (GisQL.isPresent(membership)) {
			return 1;
		} else {
			return GisQL.Missing;
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
		print.print(" =");
	}

}
