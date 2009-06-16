package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstInteractome;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;

public class ToVar extends CachedInteractome {
	private static class AstToVar implements AstNode {
		private final Environment environment;
		private final AstNode interactome;
		private final String name;

		public AstToVar(Environment environment, AstNode node, String name) {
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

		public boolean isInteractome() {
			return true;
		}

		public PrintStream show(PrintStream print) {
			interactome.show(print);
			print.print(" @ ");
			print.print(name);
			return print;
		}

		public StringBuilder show(StringBuilder sb) {
			interactome.show(sb);
			sb.append(" @ ");
			sb.append(name);
			return sb;
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

		public int getNestingLevel() {
			return 0;
		}

		public boolean isMatchingOperator(char c) {
			return c == '@';
		}

		public boolean isPrefixed() {
			return false;
		}

		public PrintStream show(PrintStream print) {
			print.print("Assign to variable: A @ varname");
			return null;
		}

		public StringBuilder show(StringBuilder sb) {
			sb.append("Assign to variable: A @ varname");
			return sb;
		}

		public Token[] tasks(Parser parser) {
			return new Token[] { new Name(parser) };
		}

	};

	private final Environment environment;

	private final String name;

	public ToVar(Environment environment, Interactome source, String name) {
		super(source, name, 0, 1);
		this.environment = environment;
		this.name = name;
	}

	public boolean postpare() {
		if (!super.postpare())
			return false;
		return environment.setVariable(name, new AstInteractome(this));
	}

	public PrintStream show(PrintStream print) {
		InteractomeUtil.precedenceShow(print, source, this.getPrecedence());
		print.print(" @ ");
		print.print(name);
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		InteractomeUtil.precedenceShow(sb, source, this.getPrecedence());
		sb.append(" @ ");
		sb.append(name);
		return sb;
	}

}
