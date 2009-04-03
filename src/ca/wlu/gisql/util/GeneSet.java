package ca.wlu.gisql.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import ca.wlu.gisql.gene.Gene;

public class GeneSet implements Iterable<Gene>, TableModel {
    static final Class[] columnClass = new Class[] { Long.class, Double.class,
	    String.class };

    static final String[] columnName = new String[] { "Identifier", "Membership",
	    "Description" };

    static final Logger log = Logger.getLogger(GeneSet.class);

    private HashMap<Long, Gene> genes = new HashMap<Long, Gene>();

    private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

    public void add(Gene gene) {
	genes.put(gene.getId(), gene);
    }

    public void addTableModelListener(TableModelListener listener) {
	listeners.add(listener);
    }

    public boolean contains(Gene gene) {
	return genes.containsValue(gene);
    }

    public Gene get(long identifier) {
	return genes.get(identifier);
    }

    public Class<?> getColumnClass(int columnIndex) {
	return columnClass[columnIndex];
    }

    public int getColumnCount() {
	return columnName.length;
    }

    public String getColumnName(int columnIndex) {
	return columnName[columnIndex];
    }

    public int getRowCount() {
	return genes.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
	Gene gene = null;
	Iterator<Gene> it = genes.values().iterator();
	while (it.hasNext() && rowIndex >= 0) {
	    gene = it.next();
	    rowIndex--;
	}

	switch (columnIndex) {
	case 0:
	    return gene.getId();
	case 1:
	    return gene.getMembership();
	case 2:
	    return gene.show(new StringBuilder()).toString();
	default:
	    return null;
	}
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
	return false;
    }

    public Iterator<Gene> iterator() {
	return genes.values().iterator();
    }

    public void notifyListeners() {
	TableModelEvent evt = new TableModelEvent(this);
	for (TableModelListener tml : listeners) {
	    tml.tableChanged(evt);
	}
    }

    public void removeTableModelListener(TableModelListener listener) {
	listeners.remove(listener);
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
	log.warn("Someone tried to edit the data.");
    }
}