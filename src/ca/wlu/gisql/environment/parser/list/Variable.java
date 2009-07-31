package ca.wlu.gisql.environment.parser.list;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class Variable implements ListParseable {

	private static final Token[] tokens = new Token[] { new Name() };

	public boolean construct(Environment environment, List<AstNode> params,
			Stack<String> error, List<AstNode> results) {
		String name = ((AstString) params.get(0)).getString();
		AstNode list = environment.getVariable(name);
		if (list != null && list instanceof AstList) {
			results.add(list);
			return true;
		} else {
			return false;
		}
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("Variable: name");
	}

	public Token[] tasks() {
		return tokens;
	}

}
