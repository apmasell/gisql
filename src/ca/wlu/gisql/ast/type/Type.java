package ca.wlu.gisql.ast.type;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowableStringBuilder;

public abstract class Type implements Show<List<TypeVariable>> {
	public static final Type BooleanType = new BooleanType();

	public static final Type FormatType = new FormatType();

	public static final Type InteractomeType = new InteractomeType();

	public static final Type MembershipType = new MembershipType();

	public static final Type NumberType = new NumberType();

	public static final Type RealType = new RealType();

	public static final Type StringType = new StringType();

	public static final Type UnitType = new UnitType();

	protected Type freshen(Type needle, Type replacement) {
		return this;
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
