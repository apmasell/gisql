package ca.wlu.gisql.gene;

import ca.wlu.gisql.interactome.Interactome;

public class DbGene implements Gene {

    private long identifier;

    private String name;

    private Interactome parent;

    public DbGene(Interactome parent, long identifier, String name) {
	this.parent = parent;
	this.identifier = identifier;
	this.name = name;
    }

    public long getId() {
	return identifier;
    }

    public double getMembership() {
	return 1;
    }

    public String getName() {
	return name;
    }

    public StringBuilder show(StringBuilder sb) {
	sb.append(identifier).append(" ");
	parent.show(sb).append(" [").append(name).append("]");
	return sb;
    }

    public String toString() {
	return show(new StringBuilder()).toString();
    }
}
