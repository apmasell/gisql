package ca.wlu.gisql.runner;

import ca.wlu.gisql.ast.AstNode;

public class AstContext extends ExpressionContext {

	private final ExpressionContext context;
	private final String node;

	public AstContext(ExpressionContext context, AstNode node) {
		this.context = context;
		this.node = node.toString();
	}

	public String getNode() {
		return node;
	}

	public ExpressionContext getParent() {
		return context;
	}

}
