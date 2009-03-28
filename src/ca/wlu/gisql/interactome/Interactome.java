package ca.wlu.gisql.interactome;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.DoubleMap;
import ca.wlu.gisql.util.Show;

public abstract class Interactome implements Show, Iterable<Interaction>,
	TableModel {

    static final Class[] columnClass = new Class[] { Long.class, Long.class,
	    Double.class, String.class };

    static final String[] columnName = new String[] { "Gene1", "Gene2",
	    "Membership", "Description" };

    static final Logger log = Logger.getLogger(Interactome.class);

    private DoubleMap<Long, Interaction> interactionLUT = new DoubleMap<Long, Interaction>();

    private List<Interaction> interactions = null;

    private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

    protected final void addInteraction(Interaction i) {
	if (interactions == null) {
	    interactions = new ArrayList<Interaction>();
	}
	interactions.add(i);
	interactionLUT.put(i.getGene1(), i.getGene2(), i);
    }

    public void addTableModelListener(TableModelListener listener) {
	listeners.add(listener);

    }

    public abstract long findOrtholog(long gene);

    public Class<?> getColumnClass(int columnIndex) {
	return columnClass[columnIndex];
    }

    public int getColumnCount() {
	return columnName.length;
    }

    public String getColumnName(int columnIndex) {
	return columnName[columnIndex];
    }

    public final Interaction getInteraction(long gene1, long gene2) {
	process();
	return interactionLUT.get(gene1, gene2);
    }

    public int getRowCount() {
	if (interactions == null) {
	    return 0;
	}
	return interactions.size();
    }

    public Object getValueAt(int rowIndex, int colIndex) {
	Interaction i = interactions.get(rowIndex);
	switch (colIndex) {
	case 0:
	    return i.getGene1();
	case 1:
	    return i.getGene2();
	case 2:
	    return i.getMembership();
	case 3:
	    return i.show(new StringBuilder()).toString();
	default:
	    return null;
	}
    }

    public boolean isCellEditable(int rowIndex, int colIndex) {
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

    public synchronized final void process() {
	if (interactions == null) {
	    interactions = new ArrayList<Interaction>();
	    prepareInteractions();
	    notifyListeners();
	}
    }

    public void removeTableModelListener(TableModelListener listener) {
	listeners.remove(listener);
    }

    public void setValueAt(Object value, int rowIndex, int colIndex) {
	log.warn("Someone tried to edit the data.");
    }
}
