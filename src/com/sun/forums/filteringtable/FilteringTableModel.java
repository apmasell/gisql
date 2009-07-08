package com.sun.forums.filteringtable;

// From http://forums.sun.com/thread.jspa?threadID=475290
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class FilteringTableModel extends AbstractTableModel implements
		TableModelListener {

	private static final long serialVersionUID = -4571408378656860875L;

	protected TableModel back = null;

	protected List<Integer> display_rows = new ArrayList<Integer>();

	protected Set<TableModelFilter> filters = new HashSet<TableModelFilter>();

	public FilteringTableModel() {
		this(null);
	}

	public FilteringTableModel(TableModel back) {
		if (back == null)
			back = new DefaultTableModel();
		this.back = back;

		refreshDisplayedRows();
		back.addTableModelListener(this);
	}

	public void addFilter(TableModelFilter filter) {
		if (filter != null) {
			filters.add(filter);
			filter.addFilteree(this);
		}
	}

	public void clearFilters() {
		filters.clear();
	}

	public Class<?> getColumnClass(int columnIndex) {
		return back.getColumnClass(columnIndex);
	}

	public int getColumnCount() {
		return back.getColumnCount();
	}

	public String getColumnName(int columnIndex) {
		return back.getColumnName(columnIndex);
	}

	public Set<TableModelFilter> getFilters() {
		return Collections.unmodifiableSet(filters);
	}

	public TableModel getRealTableModel() {
		return back;
	}

	public int getRowCount() {
		return display_rows.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return back.getValueAt(toRealRow(rowIndex), columnIndex);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return back.isCellEditable(toRealRow(rowIndex), columnIndex);
	}

	public void refreshDisplayedRows() {
		List<Integer> new_rows = new ArrayList<Integer>();
		for (int i = 0; i < back.getRowCount(); i++) {
			boolean add = true;
			for (Iterator<TableModelFilter> it = filters.iterator(); it
					.hasNext()
					&& add;) {
				TableModelFilter filter = it.next();
				if (!filter.shouldDisplay(this, back, i))
					add = false;
			}

			if (add)
				new_rows.add(new Integer(i));
		}

		display_rows = new_rows;

		fireTableDataChanged();
	}

	public void removeFilter(TableModelFilter filter) {
		if (filter != null) {
			filters.remove(filter);
			filter.removeFiltreee(this);
		}

	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		back.setValueAt(aValue, toRealRow(rowIndex), columnIndex);
	}

	public void tableChanged(TableModelEvent e) {
		refreshDisplayedRows();
		if (e.getFirstRow() == TableModelEvent.HEADER_ROW
				&& e.getLastRow() == TableModelEvent.HEADER_ROW
				&& e.getColumn() == TableModelEvent.ALL_COLUMNS) {
			fireTableStructureChanged();
		}
	}

	public int toRealRow(int display_row) {
		Integer i = display_rows.get(display_row);
		return i.intValue();
	}
}
