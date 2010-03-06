package ca.wlu.gisql.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.RecordType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;

public class ReflectedRecord implements Record {

	private final class ReflectedRecordEntry implements Entry<String, Object> {
		private final String fieldname;

		public ReflectedRecordEntry(String fieldname) {
			this.fieldname = fieldname;
		}

		@Override
		public String getKey() {
			return fieldname;
		}

		@Override
		public Object getValue() {
			return get(fieldname);
		}

		@Override
		public Object setValue(Object value) {
			return null;
		}
	}

	private static final Logger log = Logger.getLogger(ReflectedRecord.class);
	private final RecordType type = new RecordType();

	private final Object value;

	public ReflectedRecord(Object value) {
		this.value = value;
		for (Field field : value.getClass().getFields()) {
			if (Modifier.isPublic(field.getModifiers())) {
				Class<?> type = field.getType();
				if (type.isPrimitive()) {
					type = Rendering.convertPrimitive(type);
				}
				this.type.add(field.getName(), Type.convertType(type));
			}
		}
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean containsKey(Object key) {
		return type.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		for (String fieldname : type.keySet()) {
			if (get(fieldname) == value) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		Set<java.util.Map.Entry<String, Object>> set = new HashSet<Entry<String, Object>>();
		for (String fieldname : type.keySet()) {
			set.add(new ReflectedRecordEntry(fieldname));
		}
		return set;
	}

	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			return get((String) key);
		} else {
			return null;
		}
	}

	private Object get(String fieldname) {
		try {
			return value.getClass().getField(fieldname).get(value);
		} catch (IllegalArgumentException e) {
			log.error("Failed to access reflected record.", e);
		} catch (SecurityException e) {
			log.error("Failed to access reflected record.", e);
		} catch (IllegalAccessException e) {
			log.error("Failed to access reflected record.", e);
		} catch (NoSuchFieldException e) {
			log.error("Failed to access reflected record.", e);
		}
		return null;
	}

	public Type getType() {
		return type;
	}

	@Override
	public boolean isEmpty() {
		return type.isEmpty();
	}

	@Override
	public Iterator<java.util.Map.Entry<String, Object>> iterator() {
		return entrySet().iterator();
	}

	@Override
	public Set<String> keySet() {
		return type.keySet();
	}

	@Override
	public Object put(String key, Object value) {
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
	}

	@Override
	public Object remove(Object key) {
		return null;
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print("< ");
		boolean first = true;
		for (Entry<String, Object> entry : entrySet()) {
			if (first) {
				first = false;
			} else {
				print.print("; ");
			}
			print.print(entry.getKey());
			print.print(" = ");
			print.print(entry.getValue());

		}
		print.print(" >");
	}

	@Override
	public int size() {
		return type.size();
	}

	@Override
	public final String toString() {
		return ShowableStringBuilder.toString(this, null);
	}

	@Override
	public Collection<Object> values() {
		List<Object> values = new ArrayList<Object>();
		for (String fieldname : type.keySet()) {
			values.add(get(fieldname));
		}
		return values;
	}
}
