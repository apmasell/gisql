package ca.wlu.gisql.util;

public interface Mergeable<E> extends Show<E> {
	public boolean canMerge(Mergeable other);
}
