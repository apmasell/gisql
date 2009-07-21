package ca.wlu.gisql.functions;

import ca.wlu.gisql.environment.parser.ast.AstLogic;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class Branch extends Tree {

	private final Tree left;
	private final double length;
	private final Tree right;

	public Branch(Tree left, Tree right, double length) {
		super();
		this.left = left;
		this.right = right;
		this.length = length;
	}

	public AstNode getIntersection() {
		return AstLogic.makeConjunct(left.getIntersection(), right
				.getIntersection());
	}

	public AstNode getUnion() {
		return AstLogic.makeDisjunct(left.getUnion(), right.getUnion());
	}

	public void show(ShowablePrintWriter<Object> print) {
		print.print("(");
		print.print(left);
		print.print(":");
		print.print(length / 2);
		print.print(",");
		print.print(right);
		print.print(":");
		print.print(length / 2);
		print.print(")");
	}

}
