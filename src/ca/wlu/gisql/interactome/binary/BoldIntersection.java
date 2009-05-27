package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.environment.parser.util.ParseableBinaryOperation;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.BinaryArithmeticOperation;
import ca.wlu.gisql.interactome.Interactome;

public class BoldIntersection extends BinaryArithmeticOperation {

	public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
			BoldIntersection.class, 3, '⊗', new char[] { '*' },
			"Bold Intersection (0 ∨ (Ax + Bx - 1))");

	public BoldIntersection(TriangularNorm norm, Interactome left,
			Interactome right) {
		super(norm, left, right);
	}

	protected double calculateMembership(TriangularNorm norm, double left,
			double right) {
		return Math.max(0, left + right - 1);
	}

	public char getSymbol() {
		return descriptor.getSymbol();
	}

}
