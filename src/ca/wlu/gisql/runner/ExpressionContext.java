package ca.wlu.gisql.runner;

import ca.wlu.gisql.ast.AstNode;

public abstract class ExpressionContext {

	public ExpressionContext getAstContext(AstNode node) {
		return new AstContext(this, node);
	}

}
