package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;

public class Name extends Token {

	private String result;

	public Name() {
	}

	public String getResult() {
		return result;
	}

	boolean parse(Parser parser, int level, List<AstNode> results) {
		int oldposition = parser.position;
		String name = parser.parseName();
		if (name == null) {
			parser.error
					.push("Expected name missing. Position: " + oldposition);
			return false;
		}
		result = name;
		results.add(new AstString(name));
		return true;
	}

}