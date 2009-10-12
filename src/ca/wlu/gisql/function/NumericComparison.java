package ca.wlu.gisql.function;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.typeclasses.TypeClass;
import ca.wlu.gisql.vm.Machine;

public abstract class NumericComparison extends Function {

	private static final Type anumber = new TypeVariable(TypeClass.Comparable);

	public static final NumericComparison Eq = new NumericComparison("eq") {
		@Override
		public boolean compare(int difference) {
			return difference == 0;
		}
	};

	public static final NumericComparison GE = new NumericComparison("ge") {
		@Override
		public boolean compare(int difference) {
			return difference >= 0;
		}
	};
	public static final NumericComparison GT = new NumericComparison("gt") {
		@Override
		public boolean compare(int difference) {
			return difference > 0;
		}
	};
	public static final NumericComparison LE = new NumericComparison("le") {
		@Override
		public boolean compare(int difference) {
			return difference <= 0;
		}
	};
	public static final NumericComparison LT = new NumericComparison("lt") {
		@Override
		public boolean compare(int difference) {
			return difference < 0;
		}
	};
	public static final NumericComparison NE = new NumericComparison("ne") {
		@Override
		public boolean compare(int difference) {
			return difference != 0;
		}
	};

	private NumericComparison(String name) {
		super(name, "Compare to numbers in the obvious way.", anumber, anumber,
				Type.BooleanType);
	}

	public abstract boolean compare(int difference);

	@SuppressWarnings("unchecked")
	@Override
	public final Object run(Machine machine, Object... parameters) {
		return compare(((Comparable) parameters[0]).compareTo(parameters[1]));
	}
}
