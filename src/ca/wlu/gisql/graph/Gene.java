package ca.wlu.gisql.graph;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Type;
import ca.wlu.gisql.util.Show;

public class Gene implements Show, Iterable<Accession> {

	Map<Gene, Interaction> edges = new HashMap<Gene, Interaction>();

	private Set<Accession> ids = new HashSet<Accession>();

	private Map<Interactome, Double> memberships = new WeakHashMap<Interactome, Double>();

	void add(Accession accession) {
		for (Accession existingaccession : this) {
			if (existingaccession.getSpecies() == accession.getSpecies())
				throw new IllegalArgumentException("Duplicate species in gene.");
		}
		if (!ids.add(accession))
			throw new RuntimeException();
	}

	void copyMembership(Gene gene) {
		for (Entry<Interactome, Double> entry : gene.memberships.entrySet()) {
			double membership;
			Double thisMembership = memberships.get(entry.getKey());
			if (thisMembership == null || Double.isNaN(thisMembership)) {
				membership = entry.getValue();
			} else if (entry.getKey().getType() == Type.Computed) {
				membership = entry.getKey().calculateMembership(gene);
			} else {
				membership = Math.max(entry.getValue(), thisMembership);
			}
			memberships.put(entry.getKey(), membership);
		}
	}

	public Collection<Interaction> getInteractions() {
		return edges.values();
	}

	public Interaction getInteractionWith(Gene gene) {
		return edges.get(gene);
	}

	public double getMembership(Interactome interactome) {
		Double value = memberships.get(interactome);
		if (value == null)
			return Double.NaN;
		else
			return value;
	}

	public Iterator<Accession> iterator() {
		return ids.iterator();
	}

	public void setMembership(Interactome interactome, double membership) {
		memberships.put(interactome, membership);

	}

	public PrintStream show(PrintStream print) {
		boolean first = true;
		for (Accession accession : this) {
			if (!first)
				print.print(", ");
			print.print(accession.getIdentifier());
			first = false;
		}
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		boolean first = true;
		for (Accession accession : this) {
			if (!first)
				sb.append(", ");
			sb.append(accession.getIdentifier());
			first = false;
		}
		return sb;
	}
}