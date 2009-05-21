package ca.wlu.gisql.interactome;

import java.io.PrintStream;

import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;

public abstract class BinaryArithmeticOperation implements Interactome {

	private final Interactome left;

	private final TriangularNorm norm;

	private final Interactome right;

	public BinaryArithmeticOperation(TriangularNorm norm, Interactome left,
			Interactome right) {
		this.left = left;
		this.right = right;
		this.norm = norm;
	}

	public double calculateMembership(Gene gene) {
		double leftMembership = left.calculateMembership(gene);
		double rightMembership = right.calculateMembership(gene);

		if (Double.isNaN(leftMembership) && Double.isNaN(rightMembership))
			return Double.NaN;
		if (Double.isNaN(leftMembership))
			leftMembership = 0;
		if (Double.isNaN(rightMembership))
			rightMembership = 0;

		return calculateMembership(norm, leftMembership, rightMembership);
	}

	public double calculateMembership(Interaction interaction) {
		double leftMembership = left.calculateMembership(interaction);
		double rightMembership = right.calculateMembership(interaction);

		if (Double.isNaN(leftMembership) && Double.isNaN(rightMembership))
			return Double.NaN;
		if (Double.isNaN(leftMembership))
			leftMembership = 0;
		if (Double.isNaN(rightMembership))
			rightMembership = 0;

		return calculateMembership(norm, leftMembership, rightMembership);
	}

	protected abstract double calculateMembership(TriangularNorm norm,
			double left, double right);

	public abstract char getSymbol();

	public final Type getType() {
		return Type.Computed;
	}

	public final double membershipOfUnknown() {
		return calculateMembership(norm, 0, 0);
	}

	public final int numGenomes() {
		return left.numGenomes() + left.numGenomes();
	}

	public final boolean postpare() {
		return left.postpare() && right.postpare();
	}

	public final boolean prepare() {
		return left.prepare() && right.prepare();
	}

	public final PrintStream show(PrintStream print) {
		print.print("(");
		left.show(print);
		print.print(" ");
		print.print(getSymbol());
		print.print(" ");
		right.show(print);
		print.print(")");
		return print;
	}

	public final StringBuilder show(StringBuilder sb) {
		sb.append("(");
		left.show(sb);
		sb.append(" ").append(getSymbol()).append(" ");
		right.show(sb);
		sb.append(")");
		return sb;
	}
}
