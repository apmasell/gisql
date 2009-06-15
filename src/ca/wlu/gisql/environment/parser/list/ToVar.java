package ca.wlu.gisql.environment.parser.list;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.ListExpression;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;

public class ToVar implements ListParseable {

	public boolean construct(Environment environment, List<AstNode> params,
			Stack<String> error, List<AstNode> results) {
		String name = ((AstString) params.get(0)).getString();
		AstList array = (AstList) params.get(1);

		AstList stored = new AstList();
		for (AstNode node : array) {
			if (node.isInteractome()) {
				stored.add(node);
			} else {
				return false;
			}
		}
		return environment.setVariable(name, stored) && results.add(array);
	}

	public PrintStream show(PrintStream print) {
		print.print("List assignment: name = list");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("List assignment: name = list");
		return sb;
	}

	public Token[] tasks(Parser parser) {
		return new Token[] { new Name(parser), new Literal(parser, '='),
				new ListExpression(parser) };
	}

}
