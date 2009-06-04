package ca.wlu.gisql.util;

public interface Mergeable extends Show {
	public boolean canMerge(Mergeable other);
}
