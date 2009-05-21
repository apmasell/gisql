package ca.wlu.gisql.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Counter<E> implements Iterable<Map.Entry<E, Integer>> {
	private final Map<E, Integer> map = new HashMap<E, Integer>();

	private int total = 0;

	public int add(E item) {
		Integer value = map.get(item);
		if (value == null) {
			value = 1;
		} else {
			value++;
		}
		total++;
		map.put(item, value);
		return value;
	}

	public int get(E item) {
		Integer value = map.get(item);
		return (value == null ? 0 : value);
	}

	public int getTotal() {
		return total;
	}

	public Iterator<Entry<E, Integer>> iterator() {
		return map.entrySet().iterator();
	}
}
