package ca.wlu.gisql.ast.type;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.output.FileFormat;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowableStringBuilder;

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

	public boolean canUnify(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof TypeVariable) {
			return ((TypeVariable) obj).canUnify(this);
		} else {
			return false;
		}
	}

	@Override
	public Type clone() {
		List<TypeVariable> variables = new ArrayList<TypeVariable>();
		ShowableStringBuilder.toString(this, variables);
		Type result = this;
		for (TypeVariable variable : variables) {
			result = result.freshen(variable, variable.clone());
		}
		return result;
	}

	protected Type freshen(Type needle, Type replacement) {
		return this;
	}

	public int getArrowDepth() {
		return 0;
	}

	protected boolean occurs(Type needle) {
		return false;
	}

	@Override
	public String toString() {
		return ShowableStringBuilder.toString(this,
				new ArrayList<TypeVariable>());
	}

	/*
	 * Note: always do desired.unify(match) as real/membership values are
	 * sensitive.
	 */
	public boolean unify(Type that) {
		if (this == that) {
			return true;
		} else if (that instanceof TypeVariable) {
			return ((TypeVariable) that).reverseUnify(this);
		} else {
			return false;
		}
	}

	public boolean validate(Object value) {
		return false;
	}
}
