package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;

final class BooleanType extends Type {
	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print("boolean");
	}

	@Override
	public boolean validate(Object value) {
		return value instanceof Boolean;
	}
}
