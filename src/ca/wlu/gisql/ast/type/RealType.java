package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;

final class RealType extends Type {
	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print("real");
	}

	@Override
	public boolean validate(Object value) {
		return value instanceof Double;
	}
}