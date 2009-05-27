package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.environment.parser.util.ParseableBinaryOperation;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.BinaryArithmeticOperation;
import ca.wlu.gisql.interactome.Interactome;

public class Union extends BinaryArithmeticOperation {
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
