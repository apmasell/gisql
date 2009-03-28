package ca.wlu.gisql.interactome;

import ca.wlu.gisql.interaction.ComplementaryInteraction;
import ca.wlu.gisql.interaction.Interaction;

public class Intersection extends ArithmeticInteractome {

    public Intersection(Interactome left, Interactome right) {
	super(left, right);
	symbol = "\u2229";
    }

    protected double calculateMembership(Interaction j1, Interaction j2) {
	return Math.min(j1.getMembership(), j2.getMembership());
    }

    protected Interaction processLoneInteraction(Interaction j1, boolean left) {
	return new ComplementaryInteraction(j1);
    }

}
