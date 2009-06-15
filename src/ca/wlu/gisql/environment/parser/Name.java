package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;

public class Name extends Token {

	private final Parser parser;

	private String result;

	public Name(Parser parser) {
		this.parser = parser;
	}

	public String getResult() {
		return result;
	}

	boolean parse(int level, List<AstNode> results) {
		int oldposition = this.parser.position;
		String name = this.parser.parseName();
		if (name == null) {
			this.parser.error.push("Expected name missing. Position: "
					+ oldposition);
			return false;
		}
		result = name;
		results.add(new AstString(name));
		return true;
	}

}