package ca.wlu.gisql.interactome;

import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class BoldIntersection extends ArithmeticInteractome {

    public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
	    BoldIntersection.class, 3, '⊗', new char[] { '*' },
	    "Bold Intersection (0 ∨ (Ax + Bx - 1))");

    public BoldIntersection(TriangularNorm norm, Interactome left,
	    Interactome right) {
	super(norm, left, right);
    }

    public char getSymbol() {
	return descriptor.getSymbol();
    }

    protected double calculateMembership(TriangularNorm norm, double left,
	    double right) {
	return Math.max(0, left + right - 1);
    }

}
