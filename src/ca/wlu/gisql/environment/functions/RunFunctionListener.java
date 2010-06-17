package ca.wlu.gisql.environment.functions;

import java.util.Collection;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunListener;

public class RunFunctionListener implements ExpressionRunListener {
	private final ExpressionRunListener listener;

	public RunFunctionListener(ExpressionRunListener listener) {
		super();
		this.listener = listener;
	}

	@Override
	public boolean previewAst(AstNode node) {
		return true;
	}

	public void processInteractome(Interactome value) {
	}

	public void processOther(Type type, Object value) {
	}

	public void reportErrors(Collection<ExpressionError> errors) {
		listener.reportErrors(errors);
	}

}
