package ca.wlu.gisql.environment;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import org.apache.commons.collections15.iterators.IteratorChain;
import org.apache.commons.collections15.map.HashedMap;
import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.BuiltInResolver;
import ca.wlu.gisql.parser.ExpressionKnowledgebase;

/**
 * Environment hold defined variables for interactions with the user.
 * Environments can also be overlaid so that multiple environment can share
 * common definitions. All environments should have a {@link BuiltInResolver} as
 * the root environment.
 */
public abstract class Environment implements EnvironmentListener,
		Iterable<Entry<String, Object>> {
	private static final Logger log = Logger.getLogger(Environment.class);

	private final boolean alwaysPropagate;

	private final Map<EnvironmentListener, Boolean> listeners = new WeakHashMap<EnvironmentListener, Boolean>();

	private final boolean mutable;

	private final Environment parent;

	private final ExpressionKnowledgebase parserkb;

	private final Map<String, Type> types = new HashedMap<String, Type>();

	private final Map<String, Object> variables = new HashedMap<String, Object>();

	protected Environment(final Environment parent, boolean mutable,
			boolean alwaysPropagate) {
		super();
		this.parent = parent;
		this.mutable = mutable;
		this.alwaysPropagate = alwaysPropagate;
		parserkb = new ExpressionKnowledgebase();

		if (parent != null) {
			parent.addListener(this);
		}
	}

	protected final boolean add(String name, Object value, Type type) {
		Object oldvalue = variables.get(name);
		if (oldvalue != null) {
			if (!getTypeOf(name).canUnify(type) || !type.validate(value)) {
				return false;
			}

			for (EnvironmentListener listener : listeners.keySet()) {
				listener.droppedEnvironmentVariable(name, oldvalue, type);
			}
		} else {
			types.put(name, type);
		}

		variables.put(name, value);
		if (value != null) {
			for (EnvironmentListener listener : listeners.keySet()) {
				listener.addedEnvironmentVariable(name, value, type);
			}
		}
		return true;

	}

	public final void addedEnvironmentVariable(String name, Object value,
			Type type) {
		if (variables.containsKey(name)) {
			return;
		}
		for (EnvironmentListener listener : listeners.keySet()) {
			listener.addedEnvironmentVariable(name, value, type);
		}
	}

	public final void addListener(EnvironmentListener listener) {
		listeners.put(listener, Boolean.TRUE);
	}

	public final void assertWarning(String message) {
		log.warn(message);
	}

	public final void clear() {
		if (mutable) {
			variables.clear();
		}
		if (alwaysPropagate && parent != null) {
			parent.clear();
		}
	}

	public final void droppedEnvironmentVariable(String name, Object value,
			Type type) {
		if (variables.containsKey(name)) {
			return;
		}
		for (EnvironmentListener listener : listeners.keySet()) {
			listener.addedEnvironmentVariable(name, value, type);
		}
	}

	public ExpressionKnowledgebase getParserKb() {
		return parserkb;
	}

	public final Type getTypeOf(String name) {
		Type type = types.get(name);
		if (type == null && parent != null) {
			return parent.getTypeOf(name);
		} else {
			return type;
		}
	}

	public final Object getVariable(String name) {
		Object value = variables.get(name);
		if (value == null && parent != null) {
			return parent.getVariable(name);
		} else {
			return value;
		}

	}

	public final Iterator<Entry<String, Object>> iterator() {
		Iterator<Entry<String, Object>> thisit = variables.entrySet()
				.iterator();
		if (parent == null) {
			return thisit;
		}
		return new IteratorChain<Entry<String, Object>>(thisit, parent
				.iterator());
	}

	public void lastChanged() {
	}

	public final void removeListener(EnvironmentListener listener) {
		listeners.remove(listener);
	}

	public final boolean setVariable(String name, Object value, Type type) {
		if (mutable) {
			return add(name, value, type);
		} else if (alwaysPropagate && parent != null) {
			return parent.setVariable(name, value, type);
		} else {
			log
					.debug("Rejecting set of " + name + "::" + type + " to "
							+ value);
			return false;
		}
	}
}
