package ca.wlu.gisql.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.TableModel;

import com.sun.forums.filteringtable.FilteringTableModel;
import com.sun.forums.filteringtable.TableModelFilter;

public class FilterBox extends JToolBar implements ActionListener, KeyListener,
		Runnable, TableModelFilter {

	private static final long serialVersionUID = -253377732382130858L;

	private final JButton clear = new JButton(new ImageIcon(getClass()
			.getResource("images/clear.png")));

	private ScheduledFuture<?> event = null;

	private ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor();

	private Set<FilteringTableModel> filterees = new HashSet<FilteringTableModel>();

	private final JTextField search = new JTextField();

	public FilterBox() {
		super();
		this.add(new JLabel("Filter:"));
		this.add(search);
		this.add(clear);
		clear.setToolTipText("Clear");
		clear.addActionListener(this);
		search.addKeyListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clear) {
			search.setText("");
			update();
		}
	}

	public void addFilteree(FilteringTableModel filteringTableModel) {
		filterees.add(filteringTableModel);
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
		update();
	}

	public void removeFiltreee(FilteringTableModel filteringTableModel) {
		filterees.remove(filteringTableModel);
	}

	public void run() {
		for (FilteringTableModel table : filterees) {
			table.refreshDisplayedRows();
		}
	}

	public boolean shouldDisplay(FilteringTableModel source,
			TableModel base_tm, int row) {
		String text = search.getText().trim();
		if (text.length() == 0)
			return true;

		for (int column = 0; column < base_tm.getColumnCount(); column++) {
			if (base_tm.getValueAt(row, column).toString().indexOf(text) != -1)
				return true;
		}
		return false;
	}

	private void update() {
		if (event != null && !event.isDone()) {
			event.cancel(false);
		}
		event = executor.schedule(this, 500, TimeUnit.MILLISECONDS);
	}
}
