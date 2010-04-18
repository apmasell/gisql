package ca.wlu.gisql.ast.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.util.Record;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * A type representing a record. A record is something like a struct in C.
 * Records come in two flavours: want and have. A want type represents access
 * done on a map while a have type represents data stored in a map. For
 * instance, if we have <tt>(λ x. add 1 (x.foo))</tt>, then the type of <b>x</b>
 * must provide at least <tt>foo :: number</tt>, but we don't care if it
 * provides other values. Where as <tt>&lt; foo = 3 &gt;</tt> can only provide
 * <tt>foo::number</tt>.
 */
public class RecordType extends Type implements Map<String, Type> {

	private Map<String, Type> fields = new TreeMap<String, Type>();

	private Set<RecordType> siblings = new HashSet<RecordType>();

	private boolean want;

	/** Create a new “have” record type. */
	public RecordType() {
		siblings.add(this);
		want = false;
	}

	public RecordType(boolean want) {
		this();
		this.want = want;
	}

	/** Internal copy constructor. */
	private RecordType(Map<String, Type> fields, boolean want) {
		this();
		this.fields = fields;
		this.want = want;
	}

	/** Create new “want” record type containing supplied field. */
	public RecordType(String name, Type type) {
		this();
		fields.put(name, type);
		want = true;
	}

	/**
	 * Joins two record type objects so that any changes in one will propagate
	 * to the other.
	 */
	private void absorb(RecordType other) {
		siblings.addAll(other.siblings);
		for (RecordType sibling : other.siblings) {
			sibling.fields = fields;
			sibling.want = want;
		}
	}

	/**
	 * Add a new field to this record ensuring that the type matches the
	 * existing type, if any.
	 */
	public void add(String name, Type type) {
		if (fields.containsKey(name)) {
			if (!type.unify(fields.get(name))) {
				throw new IllegalArgumentException(
						"Adding member to field with type " + type
								+ " but already has field of type "
								+ fields.get(name) + ".");
			}
		} else {
			fields.put(name, type);
		}
	}

	public void clear() {
		fields.clear();
	}

	public boolean containsKey(Object key) {
		return fields.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return fields.containsValue(value);
	}

	public Set<Entry<String, Type>> entrySet() {
		return fields.entrySet();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof RecordType) {
			RecordType that = (RecordType) obj;
			return that.want == want && that.fields.equals(fields);
		} else {
			return false;
		}
	}

	@Override
	protected Type freshen(Map<Type, Type> replacement) {
		Map<String, Type> newfields = new TreeMap<String, Type>();
		for (Map.Entry<String, Type> entry : fields.entrySet()) {
			newfields
					.put(entry.getKey(), entry.getValue().freshen(replacement));
		}
		return new RecordType(newfields, want);
	}

	public Type get(Object key) {
		return fields.get(key);
	}

	@Override
	public Class<?> getRootJavaType() {
		return Record.class;
	}

	@Override
	public int hashCode() {
		return fields.hashCode() * (want ? 55 : 77);
	}

	public boolean isEmpty() {
		return fields.isEmpty();
	}

	public Set<String> keySet() {
		return fields.keySet();
	}

	@Override
	protected boolean occurs(Type needle) {
		for (Type type : fields.values()) {
			if (type.occurs(needle)) {
				return true;
			}
		}
		return false;
	}

	public Type put(String key, Type value) {
		return fields.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Type> m) {
		fields.putAll(m);
	}

	public Type remove(Object key) {
		return fields.remove(key);
	}

	@Override
	public boolean render(Rendering rendering, int depth) {
		try {
			return rendering.g$hO_CreateMap(RecordType.class, null, fields)
					&& rendering.lOhO()
					&& rendering.hO(want)
					&& rendering.g_InvokeMethod(RecordType.class.getMethod(
							"setWant", boolean.class));
		} catch (SecurityException e) {
			return false;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	@Override
	public void reset() {
		fields = new TreeMap<String, Type>();
		siblings = new HashSet<RecordType>();
		siblings.add(this);
	}

	/** For use during type reconstruction only. */
	@Deprecated
	public void setWant(boolean want) {
		this.want = want;
	}

	@Override
	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		if (want) {
			print.print("+");
		}
		print.print("< ");
		boolean first = true;
		for (Map.Entry<String, Type> entry : fields.entrySet()) {
			if (first) {
				first = false;
			} else {
				print.print("; ");
			}
			print.print(entry.getKey());
			print.print(" :: ");
			print.print(entry.getValue());
		}
		print.print(" >");
	}

	public int size() {
		return fields.size();
	}

	@Override
	public boolean unify(Type that) {
		if (this == that) {
			return true;
		} else if (that instanceof RecordType) {
			RecordType other = (RecordType) that;
			if (want && other.want) {
				/*
				 * When we unify record types that contain only desired records,
				 * we are creating a union of the two field sets. That is, we
				 * are pooling our desired fields. Any fields found in the other
				 * record type will be duplicated here and, if they are already
				 * present, the types must unify. It is best to think of a
				 * record type as having all possible labels assigned to type
				 * variables upon creation.
				 */
				for (Map.Entry<String, Type> entry : other.fields.entrySet()) {
					if (fields.containsKey(entry.getKey())) {
						if (!fields.get(entry.getKey()).unify(entry.getValue())) {
							return false;
						}
					} else {
						fields.put(entry.getKey(), entry.getValue());
					}
				}
				absorb(other);
				return true;
			} else if (!want && !other.want) {
				/*
				 * When we are unifying record type that contain constructed
				 * values, we are creating an intersection of the two field
				 * sets.
				 */
				Map<String, Type> newfields = new TreeMap<String, Type>();
				for (Map.Entry<String, Type> entry : other.fields.entrySet()) {
					if (fields.containsKey(entry.getKey())) {
						if (!fields.get(entry.getKey()).unify(entry.getValue())) {
							return false;

						} else {
							newfields.put(entry.getKey(), entry.getValue());
						}
					}
				}
				fields = newfields;
				absorb(other);
				return true;
			} else {
				/*
				 * We have disparate record types: one is a list of demands, the
				 * other is a list of resources. We must check that we can
				 * satisfy all the demands.
				 */

				Map<String, Type> demands = want ? fields : other.fields;
				Map<String, Type> resources = want ? other.fields : fields;

				for (Entry<String, Type> entry : demands.entrySet()) {
					if (resources.containsKey(entry.getKey())) {
						if (!resources.get(entry.getKey()).unify(
								entry.getValue())) {
							return false;
						}
					} else {
						return false;
					}
				}
				(want ? other : this).absorb(want ? this : other);
				return true;
			}
		} else {
			return super.unify(that);
		}
	}

	@Override
	public boolean validate(Object value) {
		if (value instanceof Record) {
			Record record = (Record) value;
			for (Map.Entry<String, Type> entry : fields.entrySet()) {
				if (!record.containsKey(entry.getKey())
						|| !entry.getValue().validate(
								record.get(entry.getKey()))) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public Collection<Type> values() {
		return fields.values();
	}

}
