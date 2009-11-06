package ca.wlu.gisql.ast.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.output.FileFormat;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowableStringBuilder;

/**
 * Representation of a type in the query language. All types in the query
 * language have (non-unique) Java representation.
 */
public abstract class Type implements Show<List<TypeVariable>> {
	public static final NativeType BooleanType = new NativeType("boolean",
			Boolean.class);

	public static final NativeType FormatType = new NativeType("format",
			FileFormat.class);

	public static final NativeType GeneType = new NativeType("gene", Gene.class);

	public static final NativeType InteractomeType = new NativeType(
			"interactome", Interactome.class);

	public static final Type MembershipType = new MembershipType();

	public static final NativeType NumberType = new NativeType("number",
			Long.class);

	public static final NativeType RealType = new NativeType("real",
			Double.class);

	public static final NativeType StringType = new NativeType("string",
			String.class);

	public static final NativeType UnitType = new NativeType("unit", Unit.class);

	/**
	 * Determines if a type can unify with another, but does not perform any
	 * substitution.
	 * 
	 * @see #unify
	 */
	public boolean canUnify(Type othertype) {
		if (this == othertype) {
			return true;
		} else if (othertype instanceof TypeVariable) {
			return ((TypeVariable) othertype).canUnify(this);
		} else {
			return false;
		}
	}

	/** Create a copy of this type with fresh type variables. */
	@Override
	public Type clone() {
		List<TypeVariable> variables = new ArrayList<TypeVariable>();
		ShowableStringBuilder.toString(this, variables);
		Map<Type, Type> replacement = new HashMap<Type, Type>();
		for (TypeVariable variable : variables) {
			replacement.put(variable, variable.clone());
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

	@Override
	public String toString() {
		return ShowableStringBuilder.toString(this,
				new ArrayList<TypeVariable>());
	}

	/**
	 * Unifies two types. This essentially checks that two types are identical
	 * and, if they can be made by substituting a value for a type variable. The
	 * substitution is made. This is the <a
	 * href="http://en.wikipedia.org/wiki/Type_inference">Hindly-Milner
	 * algorithm</a>. Extending methods must always call super.unify() to deal
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
