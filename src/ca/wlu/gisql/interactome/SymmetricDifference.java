package ca.wlu.gisql.interactome;

import ca.wlu.gisql.interaction.Interaction;

public class SymmetricDifference extends ArithmeticInteractome {

    public SymmetricDifference(Interactome left, Interactome right) {
	super(left, right);
	symbol = "∆";
    }

    protected double calculateMembership(Interaction j1, Interaction j2) {
	Double m1 = j1.getMembership();
	Double m2 = j2.getMembership();
	return Math.min(Math.min(m1, m2), 1 - Math.max(m1, m2));
    }

    protected Interaction processLoneInteraction(Interaction j1, boolean left) {
	return j1;
    }
}
