package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.environment.parser.util.ParseableBinaryOperation;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.BinaryArithmeticOperation;
import ca.wlu.gisql.interactome.Interactome;

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
