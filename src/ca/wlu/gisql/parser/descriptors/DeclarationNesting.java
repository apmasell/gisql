package ca.wlu.gisql.parser.descriptors;

import ca.wlu.gisql.util.Nextable;

public enum DeclarationNesting implements Nextable<DeclarationNesting> {
	Pair, Value;

	@Override
	public DeclarationNesting next() {
		if (this == Pair) {
			return Value;
		} else {
			return null;
		}
	}

}
