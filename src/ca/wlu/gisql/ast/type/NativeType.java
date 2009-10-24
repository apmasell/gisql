package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;

/** A query language type that is exactly the same as a Java type. */
public class NativeType extends Type {
	private final Class<?> java;

	private final String name;

	NativeType(String name, Class<?> java) {
		super();
		this.name = name;
		this.java = java;
	}

	public boolean handlesNativeType(Class<?> clazz) {
		return java.isAssignableFrom(clazz);
	}

	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print(name);
	}

	@Override
	public boolean validate(Object value) {
		return java.isAssignableFrom(value.getClass());
	}
}