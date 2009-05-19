package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.BinaryArithmeticOperation;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class StrongSymmetricDifference extends BinaryArithmeticOperation {
	public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
			StrongSymmetricDifference.class, 4, '∇', new char[] { '%' },
			"Strong Symmetric Difference (|Ax - Bx|)");

	public StrongSymmetricDifference(TriangularNorm norm, Interactome left,
			Interactome right) {
		super(norm, left, right);
	}

	protected double calculateMembership(TriangularNorm norm, double left,
			double right) {
		return Math.abs(left - right);
	}

	public char getSymbol() {
		return descriptor.getSymbol();
	}
}
