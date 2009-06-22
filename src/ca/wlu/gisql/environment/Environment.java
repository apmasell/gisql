package ca.wlu.gisql.environment;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import org.apache.commons.collections15.iterators.IteratorChain;
import org.apache.commons.collections15.map.HashedMap;

import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.ast.AstNode;

public abstract class Environment implements EnvironmentListener,
		Iterable<Entry<String, AstNode>> {
	private final boolean alwaysPropagate;

	private final Map<EnvironmentListener, Boolean> listeners = new WeakHashMap<EnvironmentListener, Boolean>();

	private final boolean mutable;

	private final Environment parent;

	private final ParserKnowledgebase parserkb;

	private final Map<String, AstNode> variables = new HashedMap<String, AstNode>();

	protected Environment(final Environment parent, boolean mutable,
			boolean alwaysPropagate) {
		super();
		this.parent = parent;
		this.mutable = mutable;
		this.alwaysPropagate = alwaysPropagate;
		this.parserkb = new ParserKnowledgebase();

		if (parent != null) {
			parent.addListener(this);
		}
	}

	protected final void add(String name, AstNode node) {
		AstNode oldnode = variables.get(name);
		if (oldnode != null) {
			for (EnvironmentListener listener : listeners.keySet()) {
				listener.droppedEnvironmentVariable(name, oldnode);
			}
		}

		variables.put(name, node);
		if (node != null) {
			for (EnvironmentListener listener : listeners.keySet()) {
				listener.addedEnvironmentVariable(name, node);
			}
		}

	}

	public final void addedEnvironmentVariable(String name, AstNode node) {
		if (variables.containsKey(name))
			return;
		for (EnvironmentListener listener : listeners.keySet()) {
			listener.addedEnvironmentVariable(name, node);
		}
	}

	public final void addListener(EnvironmentListener listener) {
		listeners.put(listener, Boolean.TRUE);
	}

	public final void clear() {
		if (mutable)
			variables.clear();
		if (alwaysPropagate && parent != null)
			parent.clear();
	}

	public final void droppedEnvironmentVariable(String name, AstNode node) {
		if (variables.containsKey(name))
			return;
		for (EnvironmentListener listener : listeners.keySet()) {
			listener.addedEnvironmentVariable(name, node);
		}
	}

	public ParserKnowledgebase getParserKb() {
		return parserkb;
	}

	public final AstNode getVariable(String name) {
		AstNode node = variables.get(name);
		if (node == null && parent != null)
			return parent.getVariable(name);
		else
			return node;

	}

	public final Iterator<Entry<String, AstNode>> iterator() {
		Iterator<Entry<String, AstNode>> thisit = variables.entrySet()
				.iterator();
		if (parent == null)
			return thisit;
		return new IteratorChain<Entry<String, AstNode>>(thisit, parent
				.iterator());
	}

	public void lastChanged() {
	}

	public final void removeListener(EnvironmentListener listener) {
		listeners.remove(listener);
	}

	public final boolean setVariable(String name, AstNode node) {
		if (mutable) {
			add(name, node);
			return true;
		} else if (alwaysPropagate && parent != null) {
			return parent.setVariable(name, node);
		} else {
			return false;
		}
	}
}
