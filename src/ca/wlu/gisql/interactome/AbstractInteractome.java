package ca.wlu.gisql.interactome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;
import org.jgrapht.EdgeFactory;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.DoubleMap;
import ca.wlu.gisql.util.GeneSet;

public abstract class AbstractInteractome implements Interactome {

    static final Class[] columnClass = new Class[] { Long.class, Long.class,
	    Double.class, String.class };

    static final String[] columnName = new String[] { "Gene1", "Gene2",
	    "Membership", "Description" };

    static final Logger log = Logger.getLogger(AbstractInteractome.class);

    private long computationTime = -1L;

    private GeneSet genes = new GeneSet();

    private DoubleMap<Gene, Interaction> interactionLUT = new DoubleMap<Gene, Interaction>();

    private ListOrderedSet<Interaction> interactions = null;

    private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

    public Interaction addEdge(Gene gene1, Gene gene2) {
	return null;
    }

    public boolean addEdge(Gene gene1, Gene gene2, Interaction interaction) {
	return false;
    }

    protected void addGene(Gene gene) {
	genes.add(gene);
    }

    protected final void addInteraction(Interaction i) {
	if (interactions == null) {
	    interactions = new ListOrderedSet<Interaction>();
	}
	interactions.add(i);
	addGene(i.getGene1());
	addGene(i.getGene2());
	interactionLUT.put(i.getGene1(), i.getGene2(), i);
    }

    public final void addTableModelListener(TableModelListener listener) {
	listeners.add(listener);
    }

    public boolean addVertex(Gene gene) {
	return false;
    }

    public boolean containsEdge(Gene gene1, Gene gene2) {
	return this.getInteraction(gene1, gene2) != null;
    }

    public boolean containsEdge(Interaction interaction) {
	return interactions.contains(interaction);
    }

    public boolean containsVertex(Gene gene) {
	return genes.contains(gene);
    }

    public int degreeOf(Gene gene) {
	return interactionLUT.getValueSetContaining(gene).size();
    }

    public Set<Interaction> edgeSet() {
	return interactions;
    }

    public Set<Interaction> edgesOf(Gene gene) {
	return interactionLUT.getValueSetContaining(gene);
    }

    public final GeneSet genes() {
	return genes;
    }

    public Set<Interaction> getAllEdges(Gene gene1, Gene gene2) {
	Set<Interaction> result = new HashSet<Interaction>();
	result.add(getInteraction(gene1, gene2));
	return result;
    }

    public final Class<?> getColumnClass(int columnIndex) {
	return columnClass[columnIndex];
    }

    public final int getColumnCount() {
	return columnName.length;
    }

    public final String getColumnName(int columnIndex) {
	return columnName[columnIndex];
    }

    public final long getComputationTime() {
	return computationTime;
    }

    public Interaction getEdge(Gene gene1, Gene gene2) {
	return getInteraction(gene1, gene2);
    }

    public EdgeFactory<Gene, Interaction> getEdgeFactory() {
	return null;
    }

    public Gene getEdgeSource(Interaction interaction) {
	return interaction.getGene1();
    }

    public Gene getEdgeTarget(Interaction interaction) {
	return interaction.getGene2();
    }

    public double getEdgeWeight(Interaction interaction) {
	return interaction.getMembership();
    }

    protected final Gene getGene(long identifier) {
	return genes.get(identifier);
    }

    public final Interaction getInteraction(Gene gene1, Gene gene2) {
	process();
	return interactionLUT.get(gene1, gene2);
    }

    public final int getRowCount() {
	if (interactions == null) {
	    return 0;
	}
	return interactions.size();
    }

    public final Object getValueAt(int rowIndex, int colIndex) {
	Interaction i = interactions.get(rowIndex);
	switch (colIndex) {
	case 0:
	    return i.getGene1().getId();
	case 1:
	    return i.getGene2().getId();
	case 2:
	    return i.getMembership();
	case 3:
	    return i.show(new StringBuilder()).toString();
	default:
	    return null;
	}
    }

    public final double hasGene(Gene gene) {
	if (genes.contains(gene)) {
	    return gene.getMembership();
	} else {
	    return 0;
	}
    }

    public final boolean isCellEditable(int rowIndex, int colIndex) {
	return false;
    }

    public final Iterator<Interaction> iterator() {
	process();
	return interactions.iterator();
    }

    private void notifyListeners() {
	TableModelEvent evt = new TableModelEvent(this);
	for (TableModelListener tml : listeners) {
	    tml.tableChanged(evt);
	}
    }

    protected abstract void prepareInteractions();

    public synchronized final boolean process() {
	if (interactions == null) {
	    long start = System.currentTimeMillis();
	    interactions = new ListOrderedSet<Interaction>();
	    prepareInteractions();
	    computationTime = System.currentTimeMillis() - start;
	    notifyListeners();
	    genes.notifyListeners();
	    return true;
	} else {
	    return false;
	}
    }

    public boolean removeAllEdges(Collection<? extends Interaction> interactions) {
	return false;
    }

    public Set<Interaction> removeAllEdges(Gene gene1, Gene gene2) {
	return null;
    }

    public boolean removeAllVertices(Collection<? extends Gene> genes) {
	return false;
    }

    public Interaction removeEdge(Gene gene1, Gene gene2) {
	return null;
    }

    public boolean removeEdge(Interaction interactions) {
	return false;
    }

    public final void removeTableModelListener(TableModelListener listener) {
	listeners.remove(listener);
    }

    public boolean removeVertex(Gene gene) {
	return false;
    }

    public final void setValueAt(Object value, int rowIndex, int colIndex) {
	log.warn("Someone tried to edit the data.");
    }

    public Set<Gene> vertexSet() {
	return genes;
    }
}
