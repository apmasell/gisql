package ca.wlu.gisql.interactome;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.wlu.gisql.interation.Interaction;
import ca.wlu.gisql.util.DoubleMap;
import ca.wlu.gisql.util.Show;

public abstract class Interactome implements Show, Iterable<Interaction> {
	private DoubleMap<Long, Interaction> interactionLUT = new DoubleMap<Long, Interaction>();

	private List<Interaction> interactions = null;

	protected final void addInteraction(Interaction i) {
		if (interactions == null) {
			interactions = new ArrayList<Interaction>();
		}
		interactions.add(i);
		interactionLUT.put(i.getGene1(), i.getGene2(), i);
	}

	public abstract long findOrtholog(long gene);

	public final Interaction getInteraction(long gene1, long gene2) {
		if (interactions == null) {
			interactions = new ArrayList<Interaction>();
			prepareInteractions();
		}
		return interactionLUT.get(gene1, gene2);
	}

	public final Iterator<Interaction> iterator() {
		if (interactions == null) {
			interactions = new ArrayList<Interaction>();
			prepareInteractions();
		}
		return interactions.iterator();
	}

	protected abstract void prepareInteractions();
}
