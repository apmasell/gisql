package ca.wlu.gisql.interactome;

import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class Residuum extends ArithmeticInteractome {
	public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
			Residuum.class, 2, '⇒', new char[] { '>' },
			"Residuum (v(Ax) s (Ax t Bx))");

	public Residuum(TriangularNorm norm, Interactome left, Interactome right) {
		super(norm, left, right);
	}

	protected double calculateMembership(TriangularNorm norm, double left,
			double right) {
		return norm.s(norm.v(left), norm.t(left, right));
	}

	public char getSymbol() {
		return descriptor.getSymbol();
	}
}
