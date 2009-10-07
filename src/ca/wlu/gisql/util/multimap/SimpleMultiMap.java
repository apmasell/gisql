package ca.wlu.gisql.util.multimap;

import java.util.Set;

public interface SimpleMultiMap {

	public abstract boolean contains(long key);

	public abstract int distanceBetween(long identfier, long identfier2);

	public abstract Set<Long> getAncestors(long key);

	public abstract Set<Long> getParents(long key);

	public abstract boolean isAncestor(long child, long parent);

}