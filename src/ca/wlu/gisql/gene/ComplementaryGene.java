package ca.wlu.gisql.gene;

public class ComplementaryGene implements Gene {

    private Gene gene;

    public ComplementaryGene(Gene gene) {
	this.gene = gene;
    }

    public long getId() {
	return gene.getId();
    }

    public double getMembership() {
	return 1 - gene.getMembership();
    }

    public String getName() {
	return gene.getName();
    }

    public StringBuilder show(StringBuilder sb) {
	sb.append("¬(");
	gene.show(sb);
	sb.append(")");
	return sb;
    }

    public String toString() {
	return show(new StringBuilder()).toString();
    }
}
