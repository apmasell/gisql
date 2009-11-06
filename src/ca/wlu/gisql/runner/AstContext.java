package ca.wlu.gisql.runner;

import ca.wlu.gisql.ast.AstNode;

/**
 * A context tied to a specific {@link AstNode}. This represents some kind of
 * semantic context.
 */
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
