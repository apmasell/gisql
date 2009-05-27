package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.environment.parser.util.ParseableBinaryOperation;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.BinaryArithmeticOperation;
import ca.wlu.gisql.interactome.Interactome;

public class SymmetricDifference extends BinaryArithmeticOperation {

	public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
			SymmetricDifference.class, 4, '∆', new char[] { '^' },
			"Symmetric Difference ((Ax t v(Bx)) s (Bx t v(Ax)))");

	public SymmetricDifference(TriangularNorm norm, Interactome left,
			Interactome right) {
		super(norm, left, right);
	}

	protected double calculateMembership(TriangularNorm norm, double left,
			double right) {
		return norm.s(norm.t(left, norm.v(right)), norm.t(right, norm.v(left)));
	}

	public char getSymbol() {
		return descriptor.getSymbol();
	}
}
