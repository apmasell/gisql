package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;

public class Expression extends Token {
	private final Parser parser;

	public Expression(Parser parser) {
		this.parser = parser;
	}

	boolean parse(int level, List<AstNode> results) {
		int oldposition = this.parser.position;
		AstNode result = (level == parser.environment.getParserKb().maxdepth ? this.parser
				.parseIdentifier()
				: this.parser.parseAutoExpression(0));
		if (result == null) {
			this.parser.error.push("Failed to parse expression. Position: "
					+ oldposition);
			return false;
		}
		results.add(result);
		return true;
	}
}