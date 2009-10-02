package ca.wlu.gisql.function.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunListener;

public class ListFromFileListener implements ExpressionRunListener {

	private List<Object> list = new ArrayList<Object>();
	private final ExpressionRunListener listener;

	public ListFromFileListener(ExpressionRunListener listener) {
		this.listener = listener;
	}

	public List<Object> getList() {
		return list;
	}

	public void processInteractome(Interactome value) {
		list.add(value);
	}

	public void processOther(Type type, Object value) {
		list.add(value);
	}

	public void reportErrors(Collection<ExpressionError> errors) {
		listener.reportErrors(errors);
	}

}
