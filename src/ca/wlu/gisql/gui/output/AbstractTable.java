package ca.wlu.gisql.gui.output;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interactome.CachedInteractome;

abstract class AbstractTable implements TableModel {

	private static final Logger log = Logger.getLogger(InteractionTable.class);

	private final Class<?>[] columnClass;

	private final String[] columnName;

	protected final CachedInteractome interactome;

	private final List<TableModelListener> listeners = new ArrayList<TableModelListener>();

	protected AbstractTable(CachedInteractome interactome,
			Class<?>[] columnClass, String[] columnNames) {
		super();
		this.interactome = interactome;
		this.columnClass = columnClass;
		this.columnName = columnNames;
	}

	public final void addTableModelListener(TableModelListener listener) {
		listeners.add(listener);
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

	public final boolean isCellEditable(int rowIndex, int colIndex) {
		return false;
	}

	public final void removeTableModelListener(TableModelListener listener) {
		listeners.remove(listener);
	}

	public final void setValueAt(Object value, int rowIndex, int colIndex) {
		log.warn("Someone tried to edit the data.");
	}

}