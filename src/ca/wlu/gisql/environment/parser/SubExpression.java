package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;

public class SubExpression extends Token {
	public static final SubExpression self = new SubExpression();

	private SubExpression() {
		super();
	}

	boolean parse(Parser parser, int level, List<AstNode> results) {
		AstNode result = (level == parser.environment.getParserKb().maxdepth ? parser
				.parseIdentifier()
				: parser.parseAutoExpression(level + 1));
		if (result == null)
			return false;
		results.add(result);
		return true;
	}
}