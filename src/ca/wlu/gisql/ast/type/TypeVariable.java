package ca.wlu.gisql.ast.type;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.typeclasses.TypeClass;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class TypeVariable extends Type {
	private Type self = null;
	private final Set<TypeClass<?>> typeclasses = new HashSet<TypeClass<?>>();

	public TypeVariable() {
		super();
	}

	public TypeVariable(TypeClass<?> typeclass) {
		super();
		typeclasses.add(typeclass);
	}

	@Override
	public TypeVariable clone() {
		TypeVariable variable = new TypeVariable();
		variable.typeclasses.addAll(typeclasses);
		return variable;
	}

	@Override
	public boolean equals(Object obj) {
		return self == null && obj instanceof Type || self.equals(obj)
				&& TypeClass.hasInstance((Type) obj, typeclasses);
	}

	@Override
	protected Type freshen(Type needle, Type replacement) {
		if (this == needle) {
			return replacement;
		} else {

			return this;
		}
	}

	public Type getDeterminedType() {
		return self;
	}

	@Override
	protected boolean isArrow() {
		return self == null ? false : self.isArrow();
	}

	@Override
	protected boolean occurs(Type needle) {
		return this == needle;
	}

	protected boolean reverseUnify(Type that) {
		return unify(that, false);
	}

	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		if (self == null) {
			int index = print.getContext().indexOf(this);
			if (index == -1) {
				index = print.getContext().size();
				print.getContext().add(this);
			}
			if (!typeclasses.isEmpty()) {
				boolean first = true;
				for (TypeClass<?> typeclass : typeclasses) {
					if (first) {
						first = false;
					} else {
						print.print('+');
					}
					print.print(typeclass);
				}
				print.print('<');
			}
			print.print((char) ('α' + index));
			if (!typeclasses.isEmpty()) {
				print.print('>');
			}
		} else {
			print.print(self);
		}
	}

	@Override
	public boolean unify(Type that) {
		return unify(that, true);
	}

	private boolean unify(Type that, boolean direction) {
		if (this == that) {
			return true;
		} else if (that.occurs(this)) {
			return false;
		} else if (self == null) {
			if (that instanceof TypeVariable) {
				TypeVariable other = (TypeVariable) that;
				if (typeclasses.size() > 0 || other.typeclasses.size() > 0) {
					other.typeclasses.addAll(typeclasses);
					typeclasses.addAll(other.typeclasses);
					Set<Type> types = TypeClass
							.matchingTypes(other.typeclasses);
					if (types.isEmpty()) {
						return false;
					} else if (types.size() == 1) {
						for (Type t : types) {
							self = t;
							other.self = t;
							return true;
						}
					}
				}
			}

			if (TypeClass.hasInstance(that, typeclasses)) {
				self = that;
				return true;
			} else {
				return false;
			}
		} else {
			return direction ? self.unify(that) : that.unify(self);
		}
	}

	@Override
	public boolean validate(Object value) {
		return self == null || self.validate(value);
	}

}