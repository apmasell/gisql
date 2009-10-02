package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;

public class MembershipType extends Type {
	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print("membership");
	}

	@Override
	public boolean unify(Type that) {
		if (that == Type.RealType) {
			return true;
		} else {
			return super.unify(that);
		}
	}

	@Override
	public boolean validate(Object value) {
		if (value instanceof Double) {
			double d = (Double) value;
			return d >= 0.0 && d <= 1.0;
		}
		return false;
	}

}
