package ca.wlu.gisql.util;

import java.util.HashMap;
import java.util.Map;

public class DoubleMap<K, V> {
    private Map<K, Map<K, V>> real = new HashMap<K, Map<K, V>>();

    public DoubleMap() {
    }

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

	submap = real.get(subkey);
	if (submap != null)
	    return submap.get(key);
	else
	    return null;

    }

    public boolean isEmpty() {
	for (Map<K, V> submap : real.values()) {
	    if (!submap.isEmpty())
		return false;
	}
	return true;
    }

    public V put(K key, K subkey, V value) {
	Map<K, V> submap = real.get(key);
	if (submap == null) {
	    submap = new HashMap<K, V>();
	    real.put(key, submap);
	}
	return submap.put(subkey, value);
    }

    public V remove(V value) {
	for (Map<K, V> submap : real.values()) {
	    submap.remove(value);
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
