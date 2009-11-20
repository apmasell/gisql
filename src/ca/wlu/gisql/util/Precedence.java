/**
 * Used for controlling the nesting of syntax in many places.
 */
package ca.wlu.gisql.util;

public enum Precedence {
	Assignment, Channel, Closure, Difference, Disjunction, Junction, UnaryPostfix, UnaryPrefix, Value;
	public static Precedence expression() {
		return Precedence.values()[1];
	}

	public final static Precedence statement() {
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