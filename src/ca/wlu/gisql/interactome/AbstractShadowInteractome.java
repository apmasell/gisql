package ca.wlu.gisql.interactome;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;
import org.jgrapht.EdgeFactory;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.GeneSet;

public abstract class AbstractShadowInteractome implements Interactome {

    static final Logger log = Logger.getLogger(AbstractShadowInteractome.class);

    private boolean first = true;

    protected Interactome i;

    public final Interaction addEdge(Gene gene1, Gene gene2) {
	return i.addEdge(gene1, gene2);
    }

    public final boolean addEdge(Gene gene1, Gene gene2, Interaction interaction) {
	return i.addEdge(gene1, gene2, interaction);
    }

    public final void addTableModelListener(TableModelListener listener) {
	i.addTableModelListener(listener);
    }

    public final boolean addVertex(Gene gene) {
	return i.addVertex(gene);
    }

    public final boolean containsEdge(Gene gene1, Gene gene2) {
	return i.containsEdge(gene1, gene2);
    }

    public final boolean containsEdge(Interaction gene) {
	return i.containsEdge(gene);
    }

    public final boolean containsVertex(Gene gene) {
	return i.containsVertex(gene);
    }

    public int countOrthologs(Gene gene) {
	return i.countOrthologs(gene);
    }

    public final int degreeOf(Gene gene) {
	return i.degreeOf(gene);
    }

    public final Set<Interaction> edgeSet() {
	return i.edgeSet();
    }

    public final Set<Interaction> edgesOf(Gene gene) {
	return i.edgesOf(gene);
    }

    public final Gene findOrtholog(Gene gene) {
	return i.findOrtholog(gene);
    }

    public final GeneSet genes() {
	return i.genes();
    }

    public final Set<Interaction> getAllEdges(Gene gene1, Gene gene2) {
	return i.getAllEdges(gene1, gene2);
    }

    public final Class<?> getColumnClass(int columnIndex) {
	return i.getColumnClass(columnIndex);
    }

    public final int getColumnCount() {
	return i.getColumnCount();
    }

    public final String getColumnName(int columnIndex) {
	return i.getColumnName(columnIndex);
    }

    public final long getComputationTime() {
	return i.getComputationTime();
    }

    public final Interaction getEdge(Gene gene1, Gene gene2) {
	return i.getEdge(gene1, gene2);
    }

    public final EdgeFactory<Gene, Interaction> getEdgeFactory() {
	return i.getEdgeFactory();
    }

    public final Gene getEdgeSource(Interaction interaction) {
	return i.getEdgeSource(interaction);
    }

    public final Gene getEdgeTarget(Interaction interaction) {
	return i.getEdgeTarget(interaction);
    }

    public final double getEdgeWeight(Interaction interaction) {
	return i.getEdgeWeight(interaction);
    }

    public final Interaction getInteraction(Gene gene1, Gene gene2) {
	this.process();
	return i.getInteraction(gene1, gene2);
    }

    public final int getRowCount() {
	return i.getRowCount();
    }

    public final Object getValueAt(int rowIndex, int columnIndex) {
	return i.getValueAt(rowIndex, columnIndex);
    }

    public final double hasGene(Gene gene) {
	return i.hasGene(gene);
    }

    public final boolean isCellEditable(int rowIndex, int columnIndex) {
	return i.isCellEditable(rowIndex, columnIndex);
    }

    public final Iterator<Interaction> iterator() {
	this.process();
	return i.iterator();
    }

    public final int numGenomes() {
	return i.numGenomes();
    }

    protected abstract void postprocess();

    public final boolean process() {
	boolean parent = i.process();
	if (first) {
	    postprocess();
	    first = false;
	}
	return parent;
    }

    public final boolean removeAllEdges(Collection<? extends Interaction> interactions) {
	return i.removeAllEdges(interactions);
    }

    public final Set<Interaction> removeAllEdges(Gene gene1, Gene gene2) {
	return i.removeAllEdges(gene1, gene2);
    }

    public final boolean removeAllVertices(Collection<? extends Gene> genes) {
	return i.removeAllVertices(genes);
    }

    public final Interaction removeEdge(Gene gene1, Gene gene2) {
	return i.removeEdge(gene1, gene2);
    }

    public final boolean removeEdge(Interaction interaction) {
	return i.removeEdge(interaction);
    }

    public final void removeTableModelListener(TableModelListener listener) {
	i.removeTableModelListener(listener);
    }

    public final boolean removeVertex(Gene gene) {
	return i.removeVertex(gene);
    }

    public final void setValueAt(Object value, int rowIndex, int colIndex) {
	i.setValueAt(value, rowIndex, colIndex);

    }

    public final Set<Gene> vertexSet() {
	return i.vertexSet();
    }

}
