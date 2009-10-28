package ca.wlu.gisql.environment;

import ca.wlu.gisql.ast.AstNode;

/**
 * Classes wishing to know about changes to an environment should implement this
 * interface.
 */
public interface EnvironmentListener {
	public abstract void addedEnvironmentVariable(String name, AstNode node);

	public abstract void droppedEnvironmentVariable(String name, AstNode node);

	public abstract void lastChanged();
}