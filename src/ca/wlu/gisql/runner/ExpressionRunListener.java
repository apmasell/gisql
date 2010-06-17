package ca.wlu.gisql.runner;

import java.util.Collection;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;

/**
 * Implementors of this interface can be recipients for the results and error of
 * an expression being run.
 */
public interface ExpressionRunListener {

	/**
	 * If you want to peruse the abstract syntax node before it is executed, you
	 * may.
	 * 
	 * @return TODO
	 */
	boolean previewAst(AstNode node);

	/** An interactome result has been computed. */
	void processInteractome(Interactome value);

	/** A non-interactome result has been computed. */
	void processOther(Type type, Object value);

	/** An expression failed to be executed. All errors are provided. */
	void reportErrors(Collection<ExpressionError> errors);

}
