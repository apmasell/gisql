package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.interactome.output.FileFormat;
import ca.wlu.gisql.util.ShowablePrintWriter;

final class FormatType extends Type {
	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print("format");
	}

	@Override
	public boolean validate(Object value) {
		return value instanceof FileFormat;
	}
}