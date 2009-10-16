package ca.wlu.gisql.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

public abstract class LazySqlSet<E> implements Set<E> {
	private static final Logger log = Logger.getLogger(LazySqlSet.class);

	Set<E> backing = null;

	@Override
	public boolean add(E e) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return false;
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean contains(Object o) {
		if (backing == null) {
			load();
		}
		return backing.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		if (backing == null) {
			load();
		}
		return backing.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		if (backing == null) {
			load();
		}
		return backing.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		if (backing == null) {
			load();
		}
		return backing.iterator();
	}

	private void load() {
		if (backing == null) {
			backing = new HashSet<E>();
			try {
				prepare(backing);
			} catch (SQLException e) {
				log.error("Failed to prepare set", e);
			}
		}
	}

	protected abstract void prepare(Set<E> set) throws SQLException;

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public int size() {
		if (backing == null) {
			load();
		}
		return backing.size();
	}

	@Override
	public Object[] toArray() {
		if (backing == null) {
			load();
		}
		return backing.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		if (backing == null) {
			load();
		}
		return backing.toArray(a);
	}
}
