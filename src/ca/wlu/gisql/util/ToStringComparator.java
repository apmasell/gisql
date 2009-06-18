package ca.wlu.gisql.util;

import java.util.Comparator;

public class ToStringComparator implements Comparator<Object> {

	public final static ToStringComparator instance = new ToStringComparator();

	public int compare(Object o1, Object o2) {
		return o1.toString().compareToIgnoreCase(o2.toString());
	}

}
