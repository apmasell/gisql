package ca.wlu.gisql.interactome;

import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class BoundedSum extends ArithmeticInteractome {

	public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
			BoundedSum.class, 1, '+', null, "Bounded Sum (1 ∧ (Ax + Bx))");

	public BoundedSum(TriangularNorm norm, Interactome left, Interactome right) {
		super(norm, left, right);
	}

	protected double calculateMembership(TriangularNorm norm, double left,
			double right) {
		return Math.min(1, left + right);
	}

	public char getSymbol() {
		return descriptor.getSymbol();
	}

}
