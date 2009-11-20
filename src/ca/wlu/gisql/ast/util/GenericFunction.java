package ca.wlu.gisql.ast.util;

import ca.wlu.gisql.ast.type.Type;

/** Represents a function pointer. */
public interface GenericFunction {

	/** Some useful textual description of this function. */
	public String getDescription();

	/** The type of this function (i.e., parameters and argument). */
	public Type getType();

	public Object run(Object... parameters);
}
