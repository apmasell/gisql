package ca.wlu.gisql.interactome;

import ca.wlu.gisql.interation.Interaction;

public class Union extends ArithmeticInteractome {

	public Union(Interactome left, Interactome right) {
		super(left, right);
		symbol = "\u222A";
	}

	protected double calculateMembership(Interaction j1, Interaction j2) {
		return Math.max(j1.getMembership(), j2.getMembership());
	}

	protected Interaction processLoneInteraction(Interaction j1, boolean left) {
		return j1;
	}

}
