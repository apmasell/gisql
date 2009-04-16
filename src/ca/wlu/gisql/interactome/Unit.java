package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.TableModelListener;

import org.jgrapht.EdgeFactory;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.GeneSet;

public class Unit implements Interactome {

	private static final Unit self = new Unit();

	public static Interactome get() {
		return self;
	}

	private GeneSet genes = new GeneSet();

	private Collection<Interaction> interactions = new ArrayList<Interaction>();

	public final Interaction addEdge(Gene gene1, Gene gene2) {
		return null;
	}

	public final boolean addEdge(Gene gene1, Gene gene2, Interaction interaction) {
		return false;
	}

	public final void addTableModelListener(TableModelListener listener) {
	}

	public final boolean addVertex(Gene gene) {
		return false;
	}

	public final boolean containsEdge(Gene gene1, Gene gene2) {
		return false;
	}

	public final boolean containsEdge(Interaction interaction) {
		return false;
	}

	public final boolean containsVertex(Gene gene) {
		return false;
	}

	public final int countOrthologs(Gene gene) {
		return 0;
	}

	public final int degreeOf(Gene gene) {
		return 0;
	}

	public final Set<Interaction> edgeSet() {
		return null;
	}

	public final Set<Interaction> edgesOf(Gene gene) {
		return null;
	}

	public final Gene findOrtholog(Gene gene) {
		return null;
	}

	public final GeneSet genes() {
		return genes;
	}

	public final Set<Interaction> getAllEdges(Gene gene1, Gene gene2) {
		return null;
	}

	public final Class<?> getColumnClass(int column) {
		return null;
	}

	public final int getColumnCount() {
		return 0;
	}

	public final String getColumnName(int column) {
		return null;
	}

	public final long getComputationTime() {
		return 0;
	}

	public final Interaction getEdge(Gene gene1, Gene gene2) {
		return null;
	}

	public final EdgeFactory<Gene, Interaction> getEdgeFactory() {
		return null;
	}

	public final Gene getEdgeSource(Interaction interaction) {
		return interaction.getGene1();
	}

	public final Gene getEdgeTarget(Interaction interaction) {
		return interaction.getGene2();
	}

	public final double getEdgeWeight(Interaction interaction) {
		return 0;
	}

	public final Interaction getInteraction(Gene gene1, Gene gene2) {
		return null;
	}

	public final int getRowCount() {
		return 0;
	}

	public final Object getValueAt(int row, int column) {
		return null;
	}

	public final double hasGene(Gene gene) {
		return 0;
	}

	public final boolean isCellEditable(int row, int column) {
		return false;
	}

	public final Iterator<Interaction> iterator() {
		return interactions.iterator();
	}

	public final int numGenomes() {
		return 0;
	}

	public boolean process() {
		return false;
	}

	public final boolean removeAllEdges(
			Collection<? extends Interaction> interactions) {
		return false;
	}

	public final Set<Interaction> removeAllEdges(Gene gene1, Gene gene2) {
		return null;
	}

	public boolean removeAllVertices(Collection<? extends Gene> genes) {
		return false;
	}

	public final Interaction removeEdge(Gene gene1, Gene gene2) {
		return null;
	}

	public final boolean removeEdge(Interaction interaction) {
		return false;
	}

	public final void removeTableModelListener(TableModelListener listener) {
	}

	public final boolean removeVertex(Gene gene) {
		return false;
	}

	public final void setValueAt(Object value, int row, int column) {
	}

	public PrintStream show(PrintStream print) {
		print.print("∅");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("∅");
		return sb;
	}

	public final Set<Gene> vertexSet() {
		return genes;
	}

}
