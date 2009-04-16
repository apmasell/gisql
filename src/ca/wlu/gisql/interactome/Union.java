package ca.wlu.gisql.interactome;

import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class Union extends ArithmeticInteractome {
	public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
			Union.class, 2, '∪', new char[] { '|' }, "Union (Ax s Bx)");

	public Union(TriangularNorm norm, Interactome left, Interactome right) {
		super(norm, left, right);
	}

	protected double calculateMembership(TriangularNorm norm, double left,
			double right) {
		return norm.s(left, right);
	}

	public char getSymbol() {
		return descriptor.getSymbol();
	}
}
