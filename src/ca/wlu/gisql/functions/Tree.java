package ca.wlu.gisql.functions;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowableStringBuilder;

public abstract class Tree implements Show<Object> {

	public abstract AstNode getIntersection();

	public abstract AstNode getUnion();

	public final String toString() {
		return ShowableStringBuilder.toString(this, null);
	}
}
