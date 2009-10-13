package ca.wlu.gisql.ast.typeclasses;

import java.util.HashSet;
import java.util.Set;

import ca.wlu.gisql.ast.type.Type;

public class TypeClass<T> {

	@SuppressWarnings("unchecked")
	public static final TypeClass<Comparable> Comparable = new TypeClass<Comparable>(
			"Comparable", Comparable.class, Type.NumberType, Type.RealType);

	public static final TypeClass<Double> Fractional = new TypeClass<Double>(
			"Fractional", Double.class, Type.MembershipType, Type.RealType);

	public static boolean hasInstance(Type type, Set<TypeClass<?>> typeclasses) {
		if (typeclasses.size() > 0) {
			for (Type t : matchingTypes(typeclasses)) {
				if (t.canUnify(type)) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}

	public static Set<Type> matchingTypes(Set<TypeClass<?>> typeclasses) {
		Set<Type> types = new HashSet<Type>();
		boolean first = true;
		for (TypeClass<?> typeclass : typeclasses) {
			if (first) {
				types.addAll(typeclass.allowed);
				first = false;
			} else {
				types.retainAll(typeclass.allowed);
			}
		}
		return types;
	}

	private final Set<Type> allowed = new HashSet<Type>();

	private final Class<T> java;

	private final String name;

	public TypeClass(String name, Class<T> java, Type... allowed) {
		super();
		this.name = name;
		this.java = java;
		for (Type type : allowed) {
			this.allowed.add(type);
		}
	}

	public boolean checkJava(Object value) {
		return java.isAssignableFrom(value.getClass());
	}

	public boolean hasInstance(Type type) {
		for (Type t : allowed) {
			if (t.canUnify(type)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

}
