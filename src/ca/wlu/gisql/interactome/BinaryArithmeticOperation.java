package ca.wlu.gisql.interactome;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;

public abstract class BinaryArithmeticOperation implements Interactome {

	static final Logger log = Logger.getLogger(BinaryArithmeticOperation.class);

	protected Interactome left, right;

	private TriangularNorm norm;

	public BinaryArithmeticOperation(TriangularNorm norm, Interactome left,
			Interactome right) {
		this.left = left;
		this.right = right;
		this.norm = norm;
	}

	public double calculateMembership(Gene gene) {
		double leftMembership = left.calculateMembership(gene);
		double rightMembership = right.calculateMembership(gene);
		return calculateMembership(norm, leftMembership, rightMembership);
	}

	public double calculateMembership(Interaction interaction) {
		double leftMembership = left.calculateMembership(interaction);
		double rightMembership = right.calculateMembership(interaction);
		return calculateMembership(norm, leftMembership, rightMembership);
	}

	protected abstract double calculateMembership(TriangularNorm norm,
			double left, double right);

	protected double clipMembership(double membership) {
		if (membership < 0)
			return 0;
		if (membership > 1)
			return 1;
		return membership;
	}

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
