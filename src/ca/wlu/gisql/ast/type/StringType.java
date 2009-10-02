package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;

final class StringType extends Type {
	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print("string");
	}

	@Override
	public boolean validate(Object value) {
		return value instanceof String;
	}
}