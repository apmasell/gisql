/**
 * Exactly the same functionality as {@link Show}, but there is a built in concept of nesting which will result in appropriate brackets being inserted into the output. 
 */
package ca.wlu.gisql.util;

public interface Prioritizable<Context, Ordering extends Comparable<Ordering>>
		extends Show<Context> {
	public abstract Ordering getPrecedence();
}