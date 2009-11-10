/**
 * Used for controlling the nesting of syntax in many places.
 */
package ca.wlu.gisql.util;

public enum Precedence {
	Assignment, Closure, Difference, Disjunction, Junction, UnaryPostfix, UnaryPrefix, Value;
	public final static Precedence start() {
		return Precedence.values()[0];
	}

	public Precedence next() {
		int ordinal = ordinal() + 1;
		if (ordinal < Precedence.values().length) {
			return Precedence.values()[ordinal];
		} else {
			return null;
		}
	}

}