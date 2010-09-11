package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;

/** A query language type that is exactly the same as a Java type. */
public class NativeType extends Type {
	/**
	 * Do no use this in an query. This is meant for passing parsed objects as
	 * AstLiterals.
	 */
	public static final Type AmbiguousJavaType = new NativeType("ANY",
			Object.class);

	private final Class<?> java;

	private final String name;

	NativeType(String name, Class<?> java) {
		super();
		this.name = name;
		this.java = java;
	}

	@Override
	public Class<?> getRootJavaType() {
		return java;
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