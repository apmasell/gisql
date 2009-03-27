package ca.wlu.gisql.interactome;

import ca.wlu.gisql.interation.ComplementaryInteraction;
import ca.wlu.gisql.interation.Interaction;

public class Difference extends ArithmeticInteractome {

	public Difference(Interactome left, Interactome right) {
		super(left, right);
		symbol = "∖";
	}

	protected double calculateMembership(Interaction j1, Interaction j2) {
		return Math.min(j1.getMembership(), 1 - j2.getMembership());
	}

	protected Interaction processLoneInteraction(Interaction j1, boolean left) {
		if (left)
			return j1;
		else
			return new ComplementaryInteraction(j1);
	}

}
