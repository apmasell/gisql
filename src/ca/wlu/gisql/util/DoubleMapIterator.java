package ca.wlu.gisql.util;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections15.IteratorUtils;

@SuppressWarnings("unchecked")
public class DoubleMapIterator<K, V> implements Iterator<V> {
	Iterator<? extends Map<K, V>> iterator;

	Iterator<V> subiterator = IteratorUtils.EMPTY_ITERATOR;

	public DoubleMapIterator(Iterator<? extends Map<K, V>> iterator) {
		this.iterator = iterator;
	}

	public boolean hasNext() {
		while (true) {
			if (subiterator.hasNext())
				return true;
			if (!iterator.hasNext())
				return false;
			subiterator = iterator.next().values().iterator();
		}
	}

	public V next() {
		while (true) {
			if (subiterator.hasNext())
				return subiterator.next();
			if (!iterator.hasNext())
				return null;
			subiterator = iterator.next().values().iterator();
		}
	}

	public void remove() {
	}
}
