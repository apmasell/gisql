package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class TemporaryEnvironment extends Token {
	private class DebrujinAst implements AstNode {
		private final int depth;

		public DebrujinAst(final int depth) {
			super();
			this.depth = depth;
		}

		public Interactome asInteractome() {
			return null;
		}

		public AstNode fork(AstNode substitute) {
			if (depth == 1)
				return substitute;
			else
				return new DebrujinAst(depth - 1);
		}

		public int getPrecedence() {
			return Integer.MAX_VALUE;
		}

		public boolean isInteractome() {
			return true;
		}

		public void show(ShowablePrintWriter print) {
			print.print(depth);
		}
	}

	private class MaskedEnvironment extends Environment {
		private final Environment parent;

		public MaskedEnvironment(Environment parent) {
			super(parent, false, true);
			this.parent = parent;
			this.add(name.getResult(), new DebrujinAst(getDepth()));
		}

		public int getDepth() {
			if (parent instanceof MaskedEnvironment) {
				return ((MaskedEnvironment) parent).getDepth() + 1;
			} else {
				return 1;
			}
		}
	}

	private final Token expression;

	private final Name name;

	private final Parser parser;

	public TemporaryEnvironment(Parser parser, Name name, Token expression) {
		this.parser = parser;
		this.name = name;
		this.expression = expression;
	}

	boolean parse(int level, List<AstNode> results) {
		Environment oldEnvironment = parser.environment;
		parser.environment = new MaskedEnvironment(oldEnvironment);
		boolean result;

		result = expression.parse(level, results);

		parser.environment = oldEnvironment;
		return result;
	}

}