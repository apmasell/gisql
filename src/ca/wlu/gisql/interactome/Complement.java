package ca.wlu.gisql.interactome;

import ca.wlu.gisql.interaction.ComplementaryInteraction;
import ca.wlu.gisql.interaction.Interaction;

public class Complement extends Interactome {

    Interactome i;

    public Complement(Interactome i) {
	this.i = i;
    }

    public long findOrtholog(long gene) {
	return i.findOrtholog(gene);
    }

    protected void prepareInteractions() {
	for (Interaction n : i) {
	    this.addInteraction(new ComplementaryInteraction(n));
	}

    }

    public StringBuilder show(StringBuilder sb) {
	sb.append("¬(");
	i.show(sb);
	sb.append(")");
	return sb;
    }

}
