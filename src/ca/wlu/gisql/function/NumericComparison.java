package ca.wlu.gisql.function;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.vm.Machine;

public abstract class NumericComparison extends Function {
	public static final NumericComparison Eq = new NumericComparison("eq") {
		@Override
		public boolean compare(long value, long threshold) {
			return value == threshold;
		}
	};

	public static final NumericComparison GE = new NumericComparison("ge") {
		@Override
		public boolean compare(long value, long threshold) {
			return value >= threshold;
		}
	};
	public static final NumericComparison GT = new NumericComparison("gt") {
		@Override
		public boolean compare(long value, long threshold) {
			return value > threshold;
		}
	};
	public static final NumericComparison LE = new NumericComparison("le") {
		@Override
		public boolean compare(long value, long threshold) {
			return value <= threshold;
		}
	};
	public static final NumericComparison LT = new NumericComparison("lt") {
		@Override
		public boolean compare(long value, long threshold) {
			return value < threshold;
		}
	};
	public static final NumericComparison NE = new NumericComparison("ne") {
		@Override
		public boolean compare(long value, long threshold) {
			return value != threshold;
		}
	};

	private NumericComparison(String name) {
		super(name, "Compare to numbers in the obvious way.", Type.NumberType,
				Type.NumberType, Type.BooleanType);
	}

	public abstract boolean compare(long value, long threshold);

	@Override
	public final Object run(Machine machine, Object... parameters) {
		return compare((Long) parameters[0], (Long) parameters[1]);
	}
}
