package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;

final class NumberType extends Type {
	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print("number");
	}

	@Override
	public boolean validate(Object value) {
		return value instanceof Long;
	}
}