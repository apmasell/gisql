package ca.wlu.gisql.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class DoubleMap<K, V> implements Iterable<V> {
	private Map<K, Map<K, V>> real = new HashMap<K, Map<K, V>>();

	public void clear() {
		real.clear();
	}

	public boolean containsKey(K key, K subkey) {
		Map<K, V> submap = real.get(key);
		if (submap != null)
			return submap.containsKey(subkey);
		else
			return false;
	}

	public boolean containsValue(V value) {
		for (Map<K, V> submap : real.values()) {
			if (submap.containsValue(value))
				return true;
		}
		return false;
	}

	public V get(K key, K subkey) {
		Map<K, V> submap = real.get(key);
		if (submap != null)
			return submap.get(subkey);

		return null;

	}

	@SuppressWarnings("unchecked")
	public Set<K> getKeySharing(K key) {
		Map<K, V> submap = real.get(key);
		if (submap == null)
			return Collections.EMPTY_SET;

		return submap.keySet();
	}

	public Collection<V> getValueListContaining(K key) {
		Map<K, V> submap = real.get(key);
		if (submap == null)
			return null;

		return submap.values();
	}

	public Set<V> getValueSetContaining(K key) {
		Map<K, V> submap = real.get(key);
		if (submap == null)
			return null;

		return new HashSet<V>(submap.values());
	}

	public boolean isEmpty() {
		for (Map<K, V> submap : real.values()) {
			if (!submap.isEmpty())
				return false;
		}
		return true;
	}

	public Iterator<V> iterator() {
		return new DoubleMapIterator<K, V>(real.values().iterator());
	}

	public V put(K key, K subkey, V value) {
		Map<K, V> submap = real.get(key);
		if (submap == null) {
			submap = new HashMap<K, V>();
			real.put(key, submap);
		}
		return submap.put(subkey, value);
	}

	public V remove(K key, K subkey) {
		Map<K, V> submap = real.get(key);
		if (submap == null)
			return null;
		V value = submap.get(subkey);
		if (value == null)
			return null;
		submap.remove(subkey);
		if (submap.size() == 0)
			real.remove(key);
		return value;
	}

	public V remove(V value) {
		for (Entry<K, Map<K, V>> entry : real.entrySet()) {
			for (Entry<K, V> subentry : entry.getValue().entrySet())
				if (subentry.getValue() == value)
					entry.getValue().remove(subentry.getKey());
			if (entry.getValue().size() == 0) {
				real.remove(entry.getKey());
			}
		}
		return value;
	}

	public int size() {
		int sum = 0;
		for (Map<K, V> submap : real.values()) {
			sum += submap.size();
		}
		return sum;
	}
}
