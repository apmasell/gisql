package ca.wlu.gisql.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Mergeable;

class CheckedGene extends Gene {

	@Override
	void add(Accession accession) {
		checkState();
		super.add(accession);
	}

	@Override
	public boolean canMerge(Mergeable<Set<Interactome>> other) {
		checkState();
		return super.canMerge(other);
	}

	@Override
	void copyMembership(Gene gene) {
		checkState();
		super.copyMembership(gene);
	}

	@Override
	public Collection<Interaction> getInteractions() {
		checkState();
		return super.getInteractions();
	}

	@Override
	protected Interaction getInteractionWith(Gene gene) {
		checkState();
		return super.getInteractionWith(gene);
	}

	@Override
	public double getMembership(Interactome interactome) {
		checkState();
		return super.getMembership(interactome);
	}

	@Override
	public Iterator<Accession> iterator() {
		checkState();
		return super.iterator();
	}

	@Override
	public void setMembership(Interactome interactome, double membership) {
		checkState();
		super.setMembership(interactome, membership);
	}

}
