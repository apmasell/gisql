package ca.wlu.gisql.util;

/**
 * The show interface is meant to emulate Haskell's Show type-class by providing
 * an easy way to concatenate large chunks of text. It is basically an efficient
 * version of {@link java.lang.Object#toString() toString()}.
 */
public interface Show<Context> {
	public void show(ShowablePrintWriter<Context> print);
}
