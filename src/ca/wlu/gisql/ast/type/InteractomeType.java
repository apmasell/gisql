package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

final class InteractomeType extends Type {

	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print("interactome");
	}

	@Override
	public boolean validate(Object value) {
		return value instanceof Interactome;
	}
}