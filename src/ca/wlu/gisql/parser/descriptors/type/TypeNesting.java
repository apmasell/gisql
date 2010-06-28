package ca.wlu.gisql.parser.descriptors.type;

import ca.wlu.gisql.util.Nextable;

public enum TypeNesting implements Nextable<TypeNesting> {
	Arrow, Couple, Receptacle, Type;

	public TypeNesting next() {
		int ordinal = ordinal() + 1;
		if (ordinal < TypeNesting.values().length) {
			return TypeNesting.values()[ordinal];
		} else {
			return null;
		}
	}
}