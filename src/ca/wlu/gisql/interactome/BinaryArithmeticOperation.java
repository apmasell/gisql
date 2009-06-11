package ca.wlu.gisql.interactome;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;

public abstract class BinaryArithmeticOperation implements Interactome {
	private static final Logger log = Logger
			.getLogger(BinaryArithmeticOperation.class);

	private final Interactome left;

	private final TriangularNorm norm;

	private final Interactome right;

	public BinaryArithmeticOperation(TriangularNorm norm, Interactome left,
			Interactome right) {
		this.left = left;
		this.right = right;
		this.norm = norm;
	}

	public final double calculateMembership(Gene gene) {
		double leftMembership = left.calculateMembership(gene);
		double rightMembership = right.calculateMembership(gene);

		if (GisQL.isMissing(leftMembership) && GisQL.isMissing(rightMembership))
			return GisQL.Missing;
		if (GisQL.isMissing(leftMembership))
			leftMembership = left.membershipOfUnknown();
		if (GisQL.isMissing(rightMembership))
			rightMembership = right.membershipOfUnknown();

		return calculateMembership(norm, leftMembership, rightMembership);
	}

	public final double calculateMembership(Interaction interaction) {
		double leftMembership = left.calculateMembership(interaction);
		double rightMembership = right.calculateMembership(interaction);

		if (GisQL.isMissing(leftMembership) && GisQL.isMissing(rightMembership))
			return GisQL.Missing;
		if (GisQL.isMissing(leftMembership))
			leftMembership = left.membershipOfUnknown();
		if (GisQL.isMissing(rightMembership))
			rightMembership = right.membershipOfUnknown();

		return calculateMembership(norm, leftMembership, rightMembership);
	}

	protected abstract double calculateMembership(TriangularNorm norm,
			double left, double right);

	public Interactome fork(Interactome substitute) {
		Interactome left = (this.left.needsFork() ? this.left.fork(substitute)
				: this.left);
		Interactome right = (this.right.needsFork() ? this.right
				.fork(substitute) : this.right);
		try {
			return this.getClass().getConstructor(TriangularNorm.class,
					Interactome.class, Interactome.class).newInstance(norm,
					left, right);
		} catch (Exception e) {
			log.error("Instantiation error during forking.", e);
		}
		return null;
	}

	public abstract char getSymbol();

	public final Type getType() {
		return Type.Computed;
	}

	public final double membershipOfUnknown() {
		return calculateMembership(norm, 0, 0);
	}

	public boolean needsFork() {
		return left.needsFork() || right.needsFork();
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
		InteractomeUtil.precedenceShow(print, left, this.getPrecedence());

		print.print(" ");
		print.print(getSymbol());
		print.print(" ");

		InteractomeUtil.precedenceShow(print, right, this.getPrecedence());

		return print;
	}

	public final StringBuilder show(StringBuilder sb) {
		InteractomeUtil.precedenceShow(sb, left, this.getPrecedence());
		sb.append(" ").append(getSymbol()).append(" ");
		InteractomeUtil.precedenceShow(sb, right, this.getPrecedence());

		return sb;
	}
}
