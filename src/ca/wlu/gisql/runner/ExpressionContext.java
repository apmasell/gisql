package ca.wlu.gisql.runner;

import ca.wlu.gisql.ast.AstNode;

/** A representation of a sub-part of a query to help locate errors precisely. */
public abstract class ExpressionContext {

	public ExpressionContext getAstContext(AstNode node) {
		return new AstContext(this, node);
	}

}
