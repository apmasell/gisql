package ca.wlu.gisql.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.map.ListOrderedMap;

public class DoubleMap<K, V> {
    private ListOrderedMap<K, ListOrderedMap<K, V>> real = new ListOrderedMap<K, ListOrderedMap<K, V>>();

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

    public Set<K> getKeySharing(K key) {
	Map<K, V> submap = real.get(key);
	if (submap == null)
	    return null;

	return submap.keySet();
    }

    public Collection<V> getValueListContaining(K key) {
	ListOrderedMap<K, V> submap = real.get(key);
	if (submap == null)
	    return null;

	return submap.values();
    }

    public Set<V> getValueSetContaining(K key) {
	ListOrderedMap<K, V> submap = real.get(key);
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

    public V put(K key, K subkey, V value) {
	ListOrderedMap<K, V> submap = real.get(key);
	if (submap == null) {
	    submap = new ListOrderedMap<K, V>();
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
