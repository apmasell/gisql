package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.List;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.binary.Intersection;

public class ComputedInteractome implements Interactome {

	private final String expression;
	private final List<Interactome> interactomes;
	private final TriangularNorm norm;
	private final List<List<Integer>> productOfSums;
	private final List<List<Integer>> productOfSumsNegated;
	private final int size;
	private final double unknown;

	public ComputedInteractome(final TriangularNorm norm,
			final List<Interactome> interactomes,
			final List<List<Integer>> productOfSums,
			final List<List<Integer>> productOfSumsNegated) {
		super();
		this.norm = norm;
		this.interactomes = interactomes;
		this.productOfSums = productOfSums;
		this.productOfSumsNegated = productOfSumsNegated;
		this.size = prepareSize();
		this.unknown = prepareUnknown();

		StringBuilder sb = new StringBuilder();
		boolean firstSum = true;
		for (int term = 0; term < productOfSums.size(); term++) {
			if (firstSum)
				firstSum = false;
			else
				sb.append(" ∩ ");
			boolean hasBrackets = productOfSums.size() > 1
					&& productOfSums.get(term).size()
							+ productOfSumsNegated.get(term).size() > 1;
			if (hasBrackets)
				sb.append("(");
			boolean firstProduct = true;
			for (int index : productOfSums.get(term)) {
				if (firstProduct)
					firstProduct = false;
				else
					sb.append(" ∪ ");
				interactomes.get(index).show(sb);
			}

			for (int index : productOfSumsNegated.get(term)) {
				if (firstProduct)
					firstProduct = false;
				else
					sb.append(" ∩ ");
				sb.append("¬");
				interactomes.get(index).show(sb);
			}
			if (hasBrackets)
				sb.append(")");
		}
		expression = sb.toString();
	}

	private double calculateMembership(double[] memberships) {
		/* Note: Σ = s-norm; ∏ = t-norm */

		double product = 1;
		for (int term = 0; term < productOfSums.size(); term++) {
			double sum = 0;
			for (int index : productOfSums.get(term)) {
				sum = norm.s(sum, memberships[index]);
			}

			for (int index : productOfSumsNegated.get(term)) {
				sum = norm.s(sum, norm.v(memberships[index]));
			}
			product = norm.t(product, sum);
		}
		return product;
	}

	public double calculateMembership(Gene gene) {
		double[] memberships = new double[interactomes.size()];
		boolean allmissing = true;
		for (int index = 0; index < interactomes.size(); index++) {
			memberships[index] = interactomes.get(index).calculateMembership(
					gene);
			if (GisQL.isMissing(memberships[index])) {
				memberships[index] = interactomes.get(index)
						.membershipOfUnknown();
			} else {
				allmissing = false;
			}

		}
		if (allmissing)
			return GisQL.Missing;
		else
			return calculateMembership(memberships);
	}

	public double calculateMembership(Interaction interaction) {
		double[] memberships = new double[interactomes.size()];
		boolean allmissing = true;
		for (int index = 0; index < interactomes.size(); index++) {
			memberships[index] = interactomes.get(index).calculateMembership(
					interaction);
			if (GisQL.isMissing(memberships[index])) {
				memberships[index] = interactomes.get(index)
						.membershipOfUnknown();
			} else {
				allmissing = false;
			}

		}
		if (allmissing)
			return GisQL.Missing;
		else
			return calculateMembership(memberships);
	}

	public int getPrecedence() {
		return (Intersection.descriptor.getNestingLevel());
	}

	public Type getType() {
		return Type.Computed;
	}

	public double membershipOfUnknown() {
		return unknown;
	}

	public int numGenomes() {
		return size;
	}

	public boolean postpare() {
		for (Interactome interactome : interactomes) {
			if (!interactome.postpare())
				return false;
		}
		return true;
	}

	public boolean prepare() {
		for (Interactome interactome : interactomes) {
			if (!interactome.prepare())
				return false;
		}
		return true;
	}

	private int prepareSize() {
		int sum = 0;
		for (Interactome interactome : interactomes) {
			sum += interactome.numGenomes();
		}
		return sum;
	}

	private double prepareUnknown() {
		double[] unknowns = new double[interactomes.size()];
		for (int index = 0; index < interactomes.size(); index++) {
			unknowns[index] = interactomes.get(index).membershipOfUnknown();
		}
		return calculateMembership(unknowns);
	}

	public PrintStream show(PrintStream print) {
		print.print(expression);
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(expression);
		return sb;
	}
}
