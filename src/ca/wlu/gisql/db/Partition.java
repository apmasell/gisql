package ca.wlu.gisql.db;

import java.util.Set;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class Partition implements TaxonomicInteractome {

	private final Set<Gene> genes;
	private final String name;
	private final TaxonomicInteractome parent;

	public Partition(TaxonomicInteractome parent, String name, Set<Gene> genes) {
		super();
		this.parent = parent;
		this.name = name;
		this.genes = genes;
	}

	@Override
	public double calculateMembership(Gene gene) {
		return genes.contains(gene) ? parent.calculateMembership(gene) : 0.0;
	}

	@Override
	public double calculateMembership(Interaction interaction) {
		return genes.contains(interaction.getGene1())
				|| genes.contains(interaction.getGene2()) ? parent
				.calculateMembership(interaction) : 0.0;
	}

	@Override
	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return parent.collectAll(set);
	}

	@Override
	public Construction getConstruction() {
		return Construction.Species;
	}

	@Override
	public long getId() {
		return parent.getId();
	}

	@Override
	public Precedence getPrecedence() {
		return parent.getPrecedence();
	}

	@Override
	public double membershipOfUnknown() {
		return 0;
	}

	@Override
	public boolean postpare() {
		return parent.postpare();
	}

	@Override
	public boolean prepare() {
		return parent.prepare();
	}

	@Override
	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(name);
	}

	@Override
	public String toString() {
		return name;
	}
}
