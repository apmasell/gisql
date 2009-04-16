package ca.wlu.gisql.interactome;

import javax.swing.table.TableModel;

import org.jgrapht.UndirectedGraph;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.GeneSet;
import ca.wlu.gisql.util.Show;

public interface Interactome extends Iterable<Interaction>, TableModel, Show,
		UndirectedGraph<Gene, Interaction> {

	public abstract int countOrthologs(Gene gene);

	public abstract Gene findOrtholog(Gene gene);

	public abstract GeneSet genes();

	public abstract long getComputationTime();

	public abstract Interaction getInteraction(Gene gene1, Gene gene2);

	public abstract double hasGene(Gene gene);

	public abstract int numGenomes();

	public abstract boolean process();

}