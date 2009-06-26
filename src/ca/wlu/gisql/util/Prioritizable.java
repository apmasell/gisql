/**
 * 
 */
package ca.wlu.gisql.util;

public interface Prioritizable<Context> extends Show<Context> {
	public abstract int getPrecedence();
}