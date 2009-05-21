package ca.wlu.gisql.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Counter<E> implements Iterable<Map.Entry<E, Integer>> {
	private final Map<E, Integer> map = new HashMap<E, Integer>();

	private int total = 0;

	public int add(E item) {
		Integer value = get(item);
		value++;
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

	public Set<E> set() {
		return map.keySet();
	}

	public int size() {
		return map.size();
	}

	public void transfer(E victim, E item) {
		int victimValue = get(victim);
		int itemValue = get(item);
		map.put(item, victimValue + itemValue);
		map.remove(victim);
	}
}
