package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.environment.parser.util.ParseableBinaryOperation;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.BinaryArithmeticOperation;
import ca.wlu.gisql.interactome.Interactome;

public class Intersection extends BinaryArithmeticOperation {
	public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
			Intersection.class, 3, '∩', new char[] { '&' },
			"Intersection (Ax t Bx)");

	public Intersection(TriangularNorm norm, Interactome left, Interactome right) {
		super(norm, left, right);
	}

	protected double calculateMembership(TriangularNorm norm, double left,
			double right) {
		return norm.t(left, right);
	}

	public int getPrecedence() {
		return descriptor.getNestingLevel();
	}

	public char getSymbol() {
		return descriptor.getSymbol();
	}
}
