package ca.wlu.gisql.runner;

import java.util.Collection;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;

public interface ExpressionRunListener {

	void processInteractome(Interactome value);

	void processOther(Type type, Object value);

	void reportErrors(Collection<ExpressionError> errors);

}
