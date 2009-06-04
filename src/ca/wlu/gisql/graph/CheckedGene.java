package ca.wlu.gisql.graph;

import java.util.Collection;
import java.util.Iterator;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Mergeable;

public class CheckedGene extends Gene {

	void add(Accession accession) {
		checkState();
		super.add(accession);
	}

	public boolean canMerge(Mergeable other) {
		checkState();
		return super.canMerge(other);
	}

	void copyMembership(Gene gene) {
		checkState();
		super.copyMembership(gene);
	}

	public Collection<Interaction> getInteractions() {
		checkState();
		return super.getInteractions();
	}

	protected Interaction getInteractionWith(Gene gene) {
		checkState();
		return super.getInteractionWith(gene);
	}

	public double getMembership(Interactome interactome) {
		checkState();
		return super.getMembership(interactome);
	}

	public Iterator<Accession> iterator() {
		checkState();
		return super.iterator();
	}

	public void setMembership(Interactome interactome, double membership) {
		checkState();
		super.setMembership(interactome, membership);
	}

}
