package com.sun.forums.filteringtable;

import javax.swing.table.TableModel;

public interface TableModelFilter {

	void addFilteree(FilteringTableModel filteringTableModel);

	void removeFiltreee(FilteringTableModel filteringTableModel);

	boolean shouldDisplay(FilteringTableModel source, TableModel base_tm,
			int row);
}
