package ca.wlu.gisql.interactome.coreicity;

public enum NumericComparison {
	Eq {
		public boolean compare(int value, int threshold) {
			return value == threshold;
		}

		public String toString() {
			return "eq";
		}
	},
	GE {
		public boolean compare(int value, int threshold) {
			return value >= threshold;
		}

		public String toString() {
			return "ge";
		}
	},
	GT {
		public boolean compare(int value, int threshold) {
			return value > threshold;
		}

		public String toString() {
			return "gt";
		}

	},
	LE {
		public boolean compare(int value, int threshold) {
			return value <= threshold;
		}

		public String toString() {
			return "le";
		}

	},
	LT {
		public boolean compare(int value, int threshold) {
			return value < threshold;
		}

		public String toString() {
			return "lt";
		}

	},
	NE {
		public boolean compare(int value, int threshold) {
			return value != threshold;
		}

		public String toString() {
			return "ne";
		}

	};
	public static NumericComparison fromString(String descriptor) {
		if ("lt".equalsIgnoreCase(descriptor)) {
			return LT;
		} else if ("le".equalsIgnoreCase(descriptor)) {
			return LE;
		} else if ("gt".equalsIgnoreCase(descriptor)) {
			return GT;
		} else if ("ge".equalsIgnoreCase(descriptor)) {
			return GE;
		} else if ("eq".equalsIgnoreCase(descriptor)) {
			return Eq;
		} else if ("ne".equalsIgnoreCase(descriptor)) {
			return NE;
		}
		return null;
	}

	public abstract boolean compare(int value, int threshold);
}
