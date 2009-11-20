package ca.wlu.gisql.environment;

import ca.wlu.gisql.ast.type.Type;

/**
 * Classes wishing to know about changes to an environment should implement this
 * interface.
 */
public interface EnvironmentListener {
	public abstract void addedEnvironmentVariable(String name, Object value,
			Type type);

	public abstract void droppedEnvironmentVariable(String name, Object value,
			Type type);

	public abstract void lastChanged();
}