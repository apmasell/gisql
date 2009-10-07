package ca.wlu.gisql.util.multimap;

import java.util.Collections;
import java.util.Set;

public class EmptyMultiMap implements SimpleMultiMap {

	public static final EmptyMultiMap self = new EmptyMultiMap();

	private EmptyMultiMap() {
		super();
	}

	public boolean contains(long key) {
		return false;
	}

	public int distanceBetween(long identfier, long identfier2) {
		return -1;
	}

	public Set<Long> getAncestors(long key) {
		return Collections.emptySet();
	}

	public Set<Long> getParents(long key) {
		return Collections.emptySet();
	}

	public boolean isAncestor(long child, long parent) {
		return false;
	}

}
