package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.BinaryArithmeticOperation;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class Difference extends BinaryArithmeticOperation {
	public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
			Difference.class, 1, '∖', new char[] { '-', '\\' },
			"Difference (Ax t v(Bx))");

	public Difference(TriangularNorm norm, Interactome left, Interactome right) {
		super(norm, left, right);
	}

	protected double calculateMembership(TriangularNorm norm, double left,
			double right) {
		return norm.t(left, norm.v(right));
	}

	public char getSymbol() {
		return descriptor.getSymbol();
	}
}
