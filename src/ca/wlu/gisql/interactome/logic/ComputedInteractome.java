package ca.wlu.gisql.interactome.logic;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

/** Worker class representing all fuzzy set-theoretic computations. */
public class ComputedInteractome implements Interactome {

	private final String expression;
	private final List<Interactome> interactomes;
	private final List<List<Integer>> productOfSums;
	private final List<List<Integer>> productOfSumsNegated;
	private final double unknown;

	/**
	 * Create a computed interactome with the supplied terms.
	 * 
	 * @param interactomes
	 *            The interactomes used as leaves in this expression.
	 * @param productOfSums
	 *            A list of terms in product of sum form. That is, each inner
	 *            list represents a term of leaves, based on index, that will be
	 *            summed, and then all these sums will be producted.
	 * @param productOfSumsNegated
	 *            A list identical to the above, but representing terms whose
	 *            negated form is part of the term.
	 * 
	 *            Note that the length of the productOfSums must be the same as
	 *            the productOfSumsNegated, as each index in both lists,
	 *            represent one term. Also, each integer in both lists must be
	 *            an index into the interactome list.
	 * 
	 *            For example, an XOR of two interactomes would look like this:
	 *            {@code new ComputedInteractome([A,B], [[0][1]], [[1][0]])}
	 */
	public ComputedInteractome(final List<Interactome> interactomes,
			final List<List<Integer>> productOfSums,
			final List<List<Integer>> productOfSumsNegated) {
		super();
		this.interactomes = interactomes;
		this.productOfSums = productOfSums;
		this.productOfSumsNegated = productOfSumsNegated;
		unknown = prepareUnknown();

		ShowableStringBuilder<Set<Interactome>> print = new ShowableStringBuilder<Set<Interactome>>(
				Membership.collectAll(this));
		boolean firstSum = true;
		for (int term = 0; term < productOfSums.size(); term++) {
			if (firstSum) {
				firstSum = false;
			} else {
				print.print(" ∩ ");
			}
			boolean hasBrackets = productOfSums.size() > 1
					&& productOfSums.get(term).size()
							+ productOfSumsNegated.get(term).size() > 1;
			if (hasBrackets) {
				print.print("(");
			}
			boolean firstProduct = true;
			for (int index : productOfSums.get(term)) {
				if (firstProduct) {
					firstProduct = false;
				} else {
					print.print(" ∪ ");
				}
				print.print(interactomes.get(index), Union.descriptor
						.getPrecedence());
			}

			for (int index : productOfSumsNegated.get(term)) {
				if (firstProduct) {
					firstProduct = false;
				} else {
					print.print(" ∪ ");
				}
				print.print("¬");
				print.print(interactomes.get(index), Complement.descriptor
						.getPrecedence());
			}
			if (hasBrackets) {
				print.print(")");
			}
		}
		expression = print.toString();
	}

	private double calculateMembership(double[] memberships) {
		/* Note: Σ = s-norm; ∏ = t-norm */

		double product = 1;
		for (int term = 0; term < productOfSums.size(); term++) {
			double sum = 0;
			for (int index : productOfSums.get(term)) {
				sum = Math.max(sum, memberships[index]);
			}

			for (int index : productOfSumsNegated.get(term)) {
				sum = Math.max(sum, 1 - memberships[index]);
			}
			product = Math.min(product, sum);
		}
		return product;
	}

	public double calculateMembership(Gene gene) {
		double[] memberships = new double[interactomes.size()];
		boolean allmissing = true;
		for (int index = 0; index < interactomes.size(); index++) {
			memberships[index] = interactomes.get(index).calculateMembership(
					gene);
			if (Membership.isMissing(memberships[index])) {
				memberships[index] = interactomes.get(index)
						.membershipOfUnknown();
			} else {
				allmissing = false;
			}

		}
		if (allmissing) {
			return Membership.Missing;
		} else {
			return calculateMembership(memberships);
		}
	}

	public double calculateMembership(Interaction interaction) {
		double[] memberships = new double[interactomes.size()];
		boolean allmissing = true;
		for (int index = 0; index < interactomes.size(); index++) {
			memberships[index] = interactomes.get(index).calculateMembership(
					interaction);
			if (Membership.isMissing(memberships[index])) {
				memberships[index] = interactomes.get(index)
						.membershipOfUnknown();
			} else {
				allmissing = false;
			}

		}
		if (allmissing) {
			return Membership.Missing;
		} else {
			return calculateMembership(memberships);
		}
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		for (Interactome interactome : interactomes) {
			interactome.collectAll(set);
		}
		return set;
	}

	public Construction getConstruction() {
		return Construction.Computed;
	}

	public int getPrecedence() {
		return Intersection.descriptor.getPrecedence();
	}

	public double membershipOfUnknown() {
		return unknown;
	}

	public boolean postpare() {
		for (Interactome interactome : interactomes) {
			if (!interactome.postpare()) {
				return false;
			}
		}
		return true;
	}

	public boolean prepare() {
		for (Interactome interactome : interactomes) {
			if (!interactome.prepare()) {
				return false;
			}
		}
		return true;
	}

	private double prepareUnknown() {
		double[] unknowns = new double[interactomes.size()];
		for (int index = 0; index < interactomes.size(); index++) {
			unknowns[index] = interactomes.get(index).membershipOfUnknown();
		}
		return calculateMembership(unknowns);
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(expression);
	}

	@Override
	public String toString() {
		return expression;
	}
}
