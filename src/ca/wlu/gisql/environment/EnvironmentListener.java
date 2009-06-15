/**
 * 
 */
package ca.wlu.gisql.environment;

import ca.wlu.gisql.environment.parser.ast.AstNode;

public interface EnvironmentListener {
	public abstract void addedEnvironmentVariable(String name, AstNode node);

	public abstract void droppedEnvironmentVariable(String name,
			AstNode node);

	public abstract void lastChanged();
}