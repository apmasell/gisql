package ca.wlu.gisql.ast.type;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.wlu.gisql.ast.typeclasses.TypeClass;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * The query language type of an unknown/generic type. A type variable may
 * belong to one or more {@link TypeClass}es.
 */
public class TypeVariable extends Type {
	private final TypeClass<?> originaltypeclass;

	private Type self = null;

	private final Set<TypeClass<?>> typeclasses = new HashSet<TypeClass<?>>();

	public TypeVariable() {
		super();
		originaltypeclass = null;
	}

	public TypeVariable(TypeClass<?> typeclass) {
		super();
		typeclasses.add(typeclass);
		originaltypeclass = typeclass;
	}

	TypeVariable duplicate() {
		TypeVariable variable = new TypeVariable();
		variable.typeclasses.addAll(typeclasses);
		return variable;
	}

	@Override
	protected Type freshen(Map<Type, Type> replacement) {
		if (self != null) {
			return self.freshen(replacement);
		} else if (replacement.containsKey(this)) {
			return replacement.get(this);
		} else {
			throw new IllegalStateException(
					"Failed to replace a type variable during refreshing.");
		}
	}

	@Override
	public int getArrowDepth() {
		return self == null ? 0 : self.getArrowDepth();
	}

	@Override
	protected boolean occurs(Type needle) {
		return this == needle || self != null && self.occurs(needle);
	}

	@Override
	public boolean render(Rendering rendering, int depth) {
		if (self == null) {
			String uglyname = "$" + hashCode();
			if (rendering.hasReference(uglyname)) {
				return rendering.lRhO(uglyname);
			} else {
				// TODO Correctly get type classes.
				return rendering.pRg$hO_CreateObject(TypeVariable.class
						.getConstructors()[0])
						&& rendering.hR_CreateLocal(uglyname)
						&& rendering.lRhO(uglyname);

			}
		} else {
			return self.render(rendering, depth);
		}
	}

	/**
	 * Undoes the type unification of this variable. This is potentially very
	 * dangerous. Use wisely.
	 */
	@Override
	public void reset() {
		self = null;
		typeclasses.clear();
		if (originaltypeclass != null) {
			typeclasses.add(originaltypeclass);
		}

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
			return self.unify(that);
		}
	}

	@Override
	public boolean validate(Object value) {
		return self == null || self.validate(value);
	}

}