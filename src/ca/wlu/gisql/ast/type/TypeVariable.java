package ca.wlu.gisql.ast.type;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.typeclasses.TypeClass;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.parser.descriptors.type.TypeNesting;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * The query language type of an unknown/generic type. A type variable may
 * belong to one or more {@link TypeClass}es.
 */
public class TypeVariable extends Type implements Iterable<TypeClass<?>> {
	private static final Logger log = Logger.getLogger(TypeVariable.class);

	private final Set<TypeClass<?>> originaltypeclass = new HashSet<TypeClass<?>>();

	private Type self = null;

	private final Set<TypeClass<?>> typeclasses = new HashSet<TypeClass<?>>();

	public TypeVariable() {
		super();
	}

	public TypeVariable(TypeClass<?> typeclass) {
		super();
		typeclasses.add(typeclass);
		originaltypeclass.add(typeclass);
	}

	/** Used by automatic code generation only. */
	@Deprecated
	public void addTypeClass(TypeClass<?> typeclass) {
		originaltypeclass.add(typeclass);
		typeclasses.add(typeclass);
	}

	TypeVariable duplicate() {
		TypeVariable variable = new TypeVariable();
		variable.originaltypeclass.addAll(originaltypeclass);
		variable.typeclasses.addAll(typeclasses);
		return variable;
	}

	@Override
	protected void fillParameters(Type[] parameters, int index) {
		if (self == null) {
			super.fillParameters(parameters, index);
		} else {
			self.fillParameters(parameters, index);
		}
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
	public Type getContents() {
		return self == null ? null : self.getContents();
	}

	@Override
	public TypeNesting getPrecedence() {
		return self == null ? TypeNesting.Type : self.getPrecedence();
	}

	@Override
	public Class<?> getRootJavaType() {
		if (self == null) {
			return Object.class;
		} else {
			return self.getRootJavaType();
		}
	}

	@Override
	public Type getTerminal() {
		return self == null ? this : self.getTerminal();
	}

	@Override
	public Type getTerminalMaybe() {
		if (self == null) {
			return new MaybeType(this);
		} else {
			return self.getTerminalMaybe();
		}
	}

	public boolean isAssigned() {
		if (self == null) {
			return false;
		} else if (self instanceof TypeVariable) {
			return ((TypeVariable) self).isAssigned();
		} else {
			return true;
		}
	}

	@Override
	public boolean isInformational() {
		return self == null ? true : self.isInformational();
	}

	@Override
	public boolean isNullable() {
		return self == null ? false : self.isNullable();
	}

	@Override
	public boolean isOptionallyNullable() {
		return self == null ? false : self.isOptionallyNullable();
	}

	@Override
	public Iterator<TypeClass<?>> iterator() {
		return typeclasses.iterator();
	}

	@Override
	protected boolean makeNull(Type contents) {
		if (self == null) {
			return false;
		} else {
			return self.makeNull(contents);
		}
	}

	@Override
	protected boolean occurs(Type needle) {
		return self == null ? this == needle : self.occurs(needle);
	}

	@Override
	public <T> boolean render(Rendering<T> rendering, int depth) {
		if (self == null) {
			String uglyname = "$tvar" + Integer.toHexString(hashCode());
			if (rendering.hasReference(uglyname)) {
				return rendering.lRhO(uglyname);
			} else {
				if (!(rendering.pRg$hO_CreateObject(TypeVariable.class
						.getConstructors()[0]) && rendering.hR_CreateLocal(
						uglyname, TypeVariable.class))) {
					return false;
				}
				try {

					for (TypeClass<?> typeclass : typeclasses) {
						if (!(rendering.lRhO(uglyname)
								&& typeclass.render(rendering, depth) && rendering
								.g_InvokeMethod(TypeVariable.class.getMethod(
										"addTypeClass", TypeClass.class)))) {
							return false;
						}
					}
				} catch (SecurityException e) {
					return false;
				} catch (NoSuchMethodException e) {
					return false;
				}

				return rendering.lRhO(uglyname);

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
		typeclasses.addAll(originaltypeclass);
	}

	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		if (self == null) {
			int index = print.getContext().indexOf(this);
			if (index == -1) {
				index = print.getContext().size();
				print.getContext().add(this);
			}

			for (TypeClass<?> typeclass : typeclasses) {
				print.print(typeclass);
				print.print(' ');
			}

			print.print((char) ('α' + index));
		} else {
			print.print(self);
		}
	}

	@Override
	public boolean unify(Type that) {
		if (this == that) {
			return true;
		} else if (self == null) {
			if (that instanceof OptionalMaybeType) {
				if (unify(that.getContents())) {
					return true;
				}
			}
			if (that instanceof TypeVariable) {
				TypeVariable other = (TypeVariable) that;
				if (other.self == null) {
					other.typeclasses.addAll(typeclasses);
					if (other.typeclasses.size() > 0) {
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
				} else if (other.self instanceof TypeVariable) {
					return unify(other.self);
				} else if (that.occurs(this)) {
					log.debug(this + " occurs in " + that);
					return false;
				}
			} else if (that.occurs(this)) {
				log.debug(this + " occurs in " + that);
				return false;
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