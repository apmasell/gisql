package ca.wlu.gisql.ast.util;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.Environment;

/**
 * Implementors of this method provide {@link AstNode} for names during the
 * resolution phase of AST processing.
 */
public interface ResolutionEnvironment {

	public Environment getEnvironment();

	public AstNode lookup(String name);
}
