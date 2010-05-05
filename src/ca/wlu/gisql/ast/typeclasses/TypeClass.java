package ca.wlu.gisql.ast.typeclasses;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Renderable;
import ca.wlu.gisql.ast.util.Rendering;

/**
 * A <a href="http://en.wikipedia.org/wiki/Type_class">type class</a> is a group
 * of types that can all be used in certain situations. They are called traits
 * in Scala and concepts in C++0x. Each must be backed by a Java interface.
 */
public class TypeClass<T> implements Renderable {

	@SuppressWarnings("unchecked")
	public static final TypeClass<Comparable> Comparable = new TypeClass<Comparable>(
			"Comparable", Comparable.class, Type.NumberType, Type.RealType);

	public static final TypeClass<Double> Fractional = new TypeClass<Double>(
			"Fractional", Double.class, Type.MembershipType, Type.RealType);

	public static final TypeClass<Object> Logic = new TypeClass<Object>(
			"Logic", Object.class, Type.BooleanType, Type.InteractomeType,
			Type.MembershipType);

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

	/**
	 * Find the types that exist in the intersection of a list of type classes.
	 * If no type classes are provided, the result will be empty.
	 */
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
	public boolean render(Rendering rendering, int depth) {
		for (Field field : TypeClass.class.getFields()) {
			if (Modifier.isStatic(field.getModifiers())
					&& TypeClass.class.isAssignableFrom(field.getType())) {
				try {

					if (field.get(null) == this) {
						return rendering.lFhO(field);
					}
				} catch (IllegalArgumentException e) {
					return false;
				} catch (IllegalAccessException e) {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

}
