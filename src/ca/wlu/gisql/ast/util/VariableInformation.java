package ca.wlu.gisql.ast.util;

import ca.wlu.gisql.ast.type.Type;

public class VariableInformation {
	private final String name;

	private final Type type;

	public VariableInformation(String name, Type type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getInternalName() {
		return org.objectweb.asm.Type.getInternalName(type.getRootJavaType());
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

}
