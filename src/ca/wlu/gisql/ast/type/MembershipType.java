package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;

public class MembershipType extends Type {
	@Override
	public boolean equals(Object obj) {
		return obj.equals(Type.RealType) || super.equals(obj);
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
