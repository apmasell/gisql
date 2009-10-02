package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;

final class UnitType extends Type {
	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print("unit");
	}

	@Override
	public boolean validate(Object value) {
		return value instanceof Unit;
	}
}