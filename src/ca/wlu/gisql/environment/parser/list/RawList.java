package ca.wlu.gisql.environment.parser.list;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Expression;
import ca.wlu.gisql.environment.parser.ListOf;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstNode;

public class RawList implements ListParseable {

	public boolean construct(Environment environment, List<AstNode> params,
			Stack<String> error, List<AstNode> results) {
		results.add(params.get(0));
		return true;
	}

	public PrintStream show(PrintStream print) {
		print.print("List: {A, B, C, ...}");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("List: {A, B, C, ...}");
		return sb;
	}

	public Token[] tasks(Parser parser) {
		return new Token[] { new Literal(parser, '{'),
				new ListOf(parser, new Expression(parser), ','),
				new Literal(parser, '}') };
	}

}
