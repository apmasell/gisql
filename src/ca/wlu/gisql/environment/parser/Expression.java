package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;

public class Expression extends Token {

	public static final Expression self = new Expression();

	private Expression() {
		super();
	}

	boolean parse(Parser parser, int level, List<AstNode> results) {
		int oldposition = parser.position;
		AstNode result = (level == parser.environment.getParserKb().maxdepth ? parser
				.parseIdentifier()
				: parser.parseAutoExpression(0));
		if (result == null) {
			parser.error.push("Failed to parse expression. Position: "
					+ oldposition);
			return false;
		}
		results.add(result);
		return true;
	}
}