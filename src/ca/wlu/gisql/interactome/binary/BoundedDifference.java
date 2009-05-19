package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.BinaryArithmeticOperation;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class BoundedDifference extends BinaryArithmeticOperation {
	public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
			BoundedDifference.class, 1, '⊝', new char[] { '~' },
			"Bounded Difference (0 ∨ (Ax - Bx))");

	public BoundedDifference(TriangularNorm norm, Interactome left,
			Interactome right) {
		super(norm, left, right);
	}

	protected double calculateMembership(TriangularNorm norm, double left,
			double right) {
		return Math.max(0, left - right);
	}

	public char getSymbol() {
		return descriptor.getSymbol();
	}

}
