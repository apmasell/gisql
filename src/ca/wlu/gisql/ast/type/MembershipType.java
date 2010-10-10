package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;

/** The query language type of a double value ∈ [0,1]. */
public class MembershipType extends Type {

	@Override
	public Class<?> getRootJavaType() {
		return Double.class;
	}

	@Override
	public boolean isInformational() {
		return true;
	}

	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print("membership");
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
