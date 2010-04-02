package ca.wlu.gisql.ast.type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.util.Renderable;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.output.FileFormat;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowableStringBuilder;

/**
 * Representation of a type in the query language. All types in the query
 * language have (non-unique) Java representation.
 */
public abstract class Type implements Renderable, Show<List<TypeVariable>> {
	public static final NativeType BooleanType = new NativeType("boolean",
			Boolean.class);

	public static final NativeType FormatType = new NativeType("format",
			FileFormat.class);

	public static final NativeType GeneType = new NativeType("gene", Gene.class);

	public static final NativeType InteractomeType = new NativeType(
			"interactome", Interactome.class);

	private static final Logger log = Logger.getLogger(Type.class);

	public static final Type MembershipType = new MembershipType();

	public static final NativeType NumberType = new NativeType("number",
			Long.class);

	public static final NativeType RealType = new NativeType("real",
			Double.class);

	private static final Set<NativeType> registeredtypes = new HashSet<NativeType>();

	public static final NativeType StringType = new NativeType("string",
			String.class);

	public static final NativeType UnitType = new NativeType("unit", Unit.class);

	/** Use reflected Java type to determine the equivalent query language type. */
	public static Type convertType(java.lang.reflect.Type javatype) {
		if (javatype instanceof Class<?>) {
			Class<?> clazz = (Class<?>) javatype;

			for (NativeType matchtype : registeredtypes) {
				if (matchtype.handlesNativeType(clazz)) {
					return matchtype;
				}
			}

			for (Field field : Type.class.getFields()) {
				if (Modifier.isStatic(field.getModifiers())
						&& NativeType.class.isAssignableFrom(field.getType())) {
					NativeType matchtype;
					try {
						matchtype = (NativeType) field.get(null);
					} catch (IllegalArgumentException e) {
						log.error("Failed to get native type field.", e);
						return null;
					} catch (IllegalAccessException e) {
						log.error("Failed to get native type field.", e);
						return null;
					}
					if (matchtype.handlesNativeType(clazz)) {
						return matchtype;
					}
				}
			}

			throw new IllegalArgumentException("Class " + clazz.getName()
					+ " as not compatible representation");

		} else if (javatype instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType) javatype;
			Class<?> clazz = (Class<?>) ptype.getRawType();

			if (List.class.isAssignableFrom(clazz)) {
				Type contents = convertType(ptype.getActualTypeArguments()[0]);
				return new ListType(contents);
			}
			throw new IllegalArgumentException("Unknown parameterised type "
					+ ptype);
		} else {
			throw new IllegalArgumentException("Unknown argument type");
		}
	}

	/** Gets the query type with a particular name. */
	public static Type getTypeForName(String name) {
		for (NativeType type : registeredtypes) {
			if (type.toString().equals(name)) {
				return type;
			}
		}
		for (Field field : Type.class.getFields()) {
			if (Modifier.isStatic(field.getModifiers())
					&& Type.class.isAssignableFrom(field.getType())) {
				Type matchtype;
				try {
					matchtype = (Type) field.get(null);
				} catch (IllegalArgumentException e) {
					log.error("Failed to access field.", e);
					return null;
				} catch (IllegalAccessException e) {
					log.error("Failed to access field.", e);
					return null;
				}
				if (matchtype.toString().equals(name)) {
					return matchtype;
				}
			}
		}
		return null;
	}

	/** Add new type to the system. */
	public static void installType(String name, Class<?> java) {
		if (getTypeForName(name) != null) {
			throw new IllegalArgumentException("Type " + name
					+ " is already in use.");
		} else if (name.length() > 0
				&& Character.isJavaIdentifierStart(name.charAt(0))) {
			for (int index = 1; index < name.length(); index++) {
				if (!Character.isJavaIdentifierPart(name.charAt(index))) {
					throw new IllegalArgumentException(
							"Name contains invalid Java identifier.");
				}
			}
			registeredtypes.add(new NativeType(name, java));
		} else {
			throw new IllegalArgumentException(
					"Name starts with invalid Java identifier.");
		}
	}

	/**
	 * Determines if a type can unify with another, but does not perform any
	 * substitution.
	 * 
	 * @see #unify
	 */
	public final boolean canUnify(Type othertype) {
		return fresh().unify(othertype.fresh());
	}

	/** Create a copy of this type with fresh type variables. */
	public final Type fresh() {
		List<TypeVariable> variables = new ArrayList<TypeVariable>();
		ShowableStringBuilder.toString(this, variables);
		Map<Type, Type> replacement = new HashMap<Type, Type>();
		for (TypeVariable variable : variables) {
			replacement.put(variable, variable.duplicate());
		}
		return freshen(replacement);
	}

	/** Create a copy of this type object replacing type variables as necessary. */
	protected Type freshen(Map<Type, Type> replacement) {
		return this;
	}

	/**
	 * Return the number of right-hand arrow types this type represents. (e.g.,
	 * int → int → int = 2, but (int → int) → int = 1)
	 */
	public int getArrowDepth() {
		return 0;
	}

	/**
	 * Determine if a type is inside this type. This is needed to prevent "α →
	 * β" from unifying with "α", causing an infinite type.
	 */
	protected boolean occurs(Type needle) {
		return false;
	}

	/**
	 * Create code to instantiate this type object in a dynamically generated
	 * class.
	 */
	public boolean render(Rendering rendering, int depth) {
		if (registeredtypes.contains(this)) {
			try {
				return rendering.hO(toString())
						&& rendering.g_InvokeMethod(Type.class.getMethod(
								"getTypeForName", String.class));
			} catch (SecurityException e) {
				log.error("Cannot access getTypeForName", e);
			} catch (NoSuchMethodException e) {
				log.error("Cannot access getTypeForName", e);
			}
			return false;
		}
		for (Field field : Type.class.getFields()) {
			if (Modifier.isStatic(field.getModifiers())
					&& Type.class.isAssignableFrom(field.getType())) {
				try {
					if ((Type) field.get(null) == this) {
						rendering.lFhO(field);
						return true;
					}
				} catch (IllegalArgumentException e) {
					log.error("Failed to get type field.", e);

				} catch (IllegalAccessException e) {
					log.error("Failed to get type field.", e);

				}
			}
		}
		throw new IllegalArgumentException("Cannot find declaration of type "
				+ toString());
	}

	public void reset() {
	}

	@Override
	public final String toString() {
		return ShowableStringBuilder.toString(this,
				new ArrayList<TypeVariable>());
	}

	/**
	 * Unifies two types. This essentially checks that two types are identical
	 * and, if they can be made by substituting a value for a type variable. The
	 * substitution is made. This is the <a
	 * href="http://en.wikipedia.org/wiki/Type_inference">Hindly-Milner
	 * algorithm</a>. Derived classes must always call super.unify() to deal
	 * with type variables.
	 */
	public boolean unify(Type that) {
		if (this == that) {
			return true;
		} else if (that instanceof TypeVariable) {
			return that.unify(this);
		} else {
			return false;
		}
	}

	/** Determine if a Java object can be assigned to this type. */
	public boolean validate(Object value) {
		return false;
	}
}
