package ca.wlu.gisql.gene;

public class CompositeGene implements Gene {

    private Gene gene;

    private double membership;

    private Gene ortholog;

    public CompositeGene(Gene gene, Gene ortholog, double membership) {
	this.gene = gene;
	this.ortholog = ortholog;
	this.membership = membership;
    }

    public long getId() {
	return gene.getId();
    }

    public double getMembership() {

	return membership;
    }

    public String getName() {
	// TODO Can we choose the better name?
	String name = gene.getName();
	if (name == null || name.trim().length() == 0) {
	    name = ortholog.getName();
	}
	return name;
    }

    public StringBuilder show(StringBuilder sb) {
	sb.append("(");
	gene.show(sb);
	sb.append(" ≈ ");
	ortholog.show(sb);
	sb.append(") : ").append(membership);
	return sb;
    }

    public String toString() {
	return show(new StringBuilder()).toString();
    }
}
