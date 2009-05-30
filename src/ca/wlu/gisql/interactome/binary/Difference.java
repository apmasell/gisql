package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.environment.parser.util.ParseableBinaryOperation;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.BinaryArithmeticOperation;
import ca.wlu.gisql.interactome.Interactome;

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

	public int getPrecedence() {
		return descriptor.getNestingLevel();
	}

	public char getSymbol() {
		return descriptor.getSymbol();
	}
}
