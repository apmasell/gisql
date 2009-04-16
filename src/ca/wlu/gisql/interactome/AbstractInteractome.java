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
import ca.wlu.gisql.interaction.UniversalInteraction;
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

	public final Interaction addEdge(Gene gene1, Gene gene2) {
		return null;
	}

	public final boolean addEdge(Gene gene1, Gene gene2, Interaction interaction) {
		return false;
	}

	protected final void addGene(Gene gene) {
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

	public final boolean addVertex(Gene gene) {
		return false;
	}

	public final boolean containsEdge(Gene gene1, Gene gene2) {
		return this.getInteraction(gene1, gene2) != null;
	}

	public final boolean containsEdge(Interaction interaction) {
		return interactions.contains(interaction);
	}

	public final boolean containsVertex(Gene gene) {
		return genes.contains(gene);
	}

	public final int degreeOf(Gene gene) {
		return interactionLUT.getValueSetContaining(gene).size();
	}

	public final Set<Interaction> edgeSet() {
		return interactions;
	}

	public final Set<Interaction> edgesOf(Gene gene) {
		return interactionLUT.getValueSetContaining(gene);
	}

	public Gene findOrtholog(Gene gene) {
		return genes.findOrtholog(gene);
	}

	public final GeneSet genes() {
		return genes;
	}

	public final Set<Interaction> getAllEdges(Gene gene1, Gene gene2) {
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

	public final Interaction getEdge(Gene gene1, Gene gene2) {
		return getInteraction(gene1, gene2);
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
		return interaction.getMembership();
	}

	protected final Gene getGene(long identifier) {
		return genes.get(identifier);
	}

	public final Interaction getInteraction(Gene gene1, Gene gene2) {
		process();
		Interaction result = interactionLUT.get(gene1, gene2);
		if (result == null) {
			double membership = membershipOfUnknown();
			if (membership == 0)
				return null;
			return new UniversalInteraction(this, gene1, gene2, membership);
		} else {
			return result;
		}
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
			return membershipOfUnknown();
		}
	}

	public final boolean isCellEditable(int rowIndex, int colIndex) {
		return false;
	}

	public final Iterator<Interaction> iterator() {
		process();
		return interactions.iterator();
	}

	protected abstract double membershipOfUnknown();

	private final void notifyListeners() {
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

	public final boolean removeAllEdges(
			Collection<? extends Interaction> interactions) {
		return false;
	}

	public final Set<Interaction> removeAllEdges(Gene gene1, Gene gene2) {
		return null;
	}

	public final boolean removeAllVertices(Collection<? extends Gene> genes) {
		return false;
	}

	public final Interaction removeEdge(Gene gene1, Gene gene2) {
		return null;
	}

	public final boolean removeEdge(Interaction interactions) {
		return false;
	}

	public final void removeTableModelListener(TableModelListener listener) {
		listeners.remove(listener);
	}

	public final boolean removeVertex(Gene gene) {
		return false;
	}

	public final void setValueAt(Object value, int rowIndex, int colIndex) {
		log.warn("Someone tried to edit the data.");
	}

	public final Set<Gene> vertexSet() {
		return genes;
	}
}
