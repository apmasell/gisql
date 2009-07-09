package ca.wlu.gisql.gui.output;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interactome.CachedInteractome;

abstract class AbstractTable implements ListSelectionListener, MouseListener,
		TableModel {

	private static final Logger log = Logger.getLogger(InteractionTable.class);

	private final Class<?>[] columnClass;

	private final String[] columnName;

	protected final CachedInteractome interactome;

	private final List<TableModelListener> listeners = new ArrayList<TableModelListener>();

	private int selectedRow = -1;

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

	protected abstract JPopupMenu createMenu(int row);

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

	public final void mouseClicked(MouseEvent e) {
	}

	public final void mouseEntered(MouseEvent e) {
	}

	public final void mouseExited(MouseEvent e) {
	}

	public final void mousePressed(MouseEvent e) {
		showPopup(e);
	}

	public final void mouseReleased(MouseEvent e) {
		showPopup(e);
	}

	public final void removeTableModelListener(TableModelListener listener) {
		listeners.remove(listener);
	}

	public final void setValueAt(Object value, int rowIndex, int colIndex) {
		log.warn("Someone tried to edit the data.");
	}

	private final void showPopup(MouseEvent e) {
		if (e.isPopupTrigger() && selectedRow >= 0
				&& selectedRow < getRowCount()) {
			createMenu(selectedRow).show(e.getComponent(), e.getX(), e.getY());
		}

	}

	public final void valueChanged(ListSelectionEvent event) {
		selectedRow = event.getFirstIndex();
	}
}
