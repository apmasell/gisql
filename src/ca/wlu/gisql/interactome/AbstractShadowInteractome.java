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

    public Interaction addEdge(Gene gene1, Gene gene2) {
	return i.addEdge(gene1, gene2);
    }

    public boolean addEdge(Gene gene1, Gene gene2, Interaction interaction) {
	return i.addEdge(gene1, gene2, interaction);
    }

    public void addTableModelListener(TableModelListener listener) {
	i.addTableModelListener(listener);
    }

    public boolean addVertex(Gene gene) {
	return i.addVertex(gene);
    }

    public boolean containsEdge(Gene gene1, Gene gene2) {
	return i.containsEdge(gene1, gene2);
    }

    public boolean containsEdge(Interaction gene) {
	return i.containsEdge(gene);
    }

    public boolean containsVertex(Gene gene) {
	return i.containsVertex(gene);
    }

    public int degreeOf(Gene gene) {
	return i.degreeOf(gene);
    }

    public Set<Interaction> edgeSet() {
	return i.edgeSet();
    }

    public Set<Interaction> edgesOf(Gene gene) {
	return i.edgesOf(gene);
    }

    public Gene findOrtholog(Gene gene) {
	return i.findOrtholog(gene);
    }

    public GeneSet genes() {
	return i.genes();
    }

    public Set<Interaction> getAllEdges(Gene gene1, Gene gene2) {
	return i.getAllEdges(gene1, gene2);
    }

    public Class<?> getColumnClass(int columnIndex) {
	return i.getColumnClass(columnIndex);
    }

    public int getColumnCount() {
	return i.getColumnCount();
    }

    public String getColumnName(int columnIndex) {
	return i.getColumnName(columnIndex);
    }

    public long getComputationTime() {
	return i.getComputationTime();
    }

    public Interaction getEdge(Gene gene1, Gene gene2) {
	return i.getEdge(gene1, gene2);
    }

    public EdgeFactory<Gene, Interaction> getEdgeFactory() {
	return i.getEdgeFactory();
    }

    public Gene getEdgeSource(Interaction interaction) {
	return i.getEdgeSource(interaction);
    }

    public Gene getEdgeTarget(Interaction interaction) {
	return i.getEdgeTarget(interaction);
    }

    public double getEdgeWeight(Interaction interaction) {
	return i.getEdgeWeight(interaction);
    }

    public Interaction getInteraction(Gene gene1, Gene gene2) {
	this.process();
	return i.getInteraction(gene1, gene2);
    }

    public int getRowCount() {
	return i.getRowCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
	return i.getValueAt(rowIndex, columnIndex);
    }

    public double hasGene(Gene gene) {
	return i.hasGene(gene);
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
	return i.isCellEditable(rowIndex, columnIndex);
    }

    public Iterator<Interaction> iterator() {
	this.process();
	return i.iterator();
    }

    public int numGenomes() {
	return i.numGenomes();
    }

    protected abstract void postprocess();

    public boolean process() {
	boolean parent = i.process();
	if (first) {
	    postprocess();
	    first = false;
	}
	return parent;
    }

    public boolean removeAllEdges(Collection<? extends Interaction> interactions) {
	return i.removeAllEdges(interactions);
    }

    public Set<Interaction> removeAllEdges(Gene gene1, Gene gene2) {
	return i.removeAllEdges(gene1, gene2);
    }

    public boolean removeAllVertices(Collection<? extends Gene> genes) {
	return i.removeAllVertices(genes);
    }

    public Interaction removeEdge(Gene gene1, Gene gene2) {
	return i.removeEdge(gene1, gene2);
    }

    public boolean removeEdge(Interaction interaction) {
	return i.removeEdge(interaction);
    }

    public void removeTableModelListener(TableModelListener listener) {
	i.removeTableModelListener(listener);
    }

    public boolean removeVertex(Gene gene) {
	return i.removeVertex(gene);
    }

    public void setValueAt(Object value, int rowIndex, int colIndex) {
	i.setValueAt(value, rowIndex, colIndex);

    }

    public Set<Gene> vertexSet() {
	return i.vertexSet();
    }

}
