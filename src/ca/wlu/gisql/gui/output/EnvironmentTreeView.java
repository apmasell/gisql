package ca.wlu.gisql.gui.output;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.EnvironmentListener;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Type;

public class EnvironmentTreeView extends DefaultMutableTreeNode implements
		EnvironmentListener {

	public static class AstNodeTreeNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = 6806723183738094759L;
		private AstNode node;

		AstNodeTreeNode(String name, AstNode node) {
			super(name);
			this.node = node;
		}

		public AstNode getNode() {
			return node;
		}
	}

	private static final long serialVersionUID = -4745797101983139778L;

	private final Environment environment;

	public EnvironmentTreeView(Environment environment) {
		super("Environment");
		this.environment = environment;
		environment.addListener(this);
		prepareTree();
	}

	public void addedEnvironmentVariable(String name, AstNode node) {
		prepareTree();
	}

	private void appendFromMap(String name, SortedMap<String, AstNode> map) {
		if (map.size() == 0)
			return;
		DefaultMutableTreeNode tree = new DefaultMutableTreeNode(name);
		for (Entry<String, AstNode> entry : map.entrySet()) {
			DefaultMutableTreeNode child = new AstNodeTreeNode(entry.getKey(),
					entry.getValue());
			tree.add(child);
		}
		this.add(tree);
	}

	public void droppedEnvironmentVariable(String name, AstNode node) {
		prepareTree();
	}

	public void lastChanged() {
		prepareTree();
	}

	private void prepareTree() {
		this.removeAllChildren();
		SortedMap<String, AstNode> other = new TreeMap<String, AstNode>();
		SortedMap<String, MutableTreeNode> lists = new TreeMap<String, MutableTreeNode>();
		SortedMap<String, AstNode> species = new TreeMap<String, AstNode>();
		SortedMap<String, AstNode> named = new TreeMap<String, AstNode>();
		SortedMap<String, AstNode> appended = new TreeMap<String, AstNode>();
		for (Entry<String, AstNode> entry : environment) {

			if (entry.getValue().isInteractome()) {
				if (entry.getKey().startsWith("_")) {
					appended.put(entry.getKey(), entry.getValue());
				} else {
					Interactome interactome = entry.getValue().asInteractome();
					(interactome.getType() == Type.Species ? species : named)
							.put(entry.getKey(), entry.getValue());
				}
			} else if (entry.getValue() instanceof AstList) {
				DefaultMutableTreeNode list = new DefaultMutableTreeNode(entry
						.getKey());
				for (AstNode node : (AstList) entry.getValue()) {
					list.add(new AstNodeTreeNode(node.toString(), node));
				}
				lists.put(entry.getKey(), list);
			} else {
				other.put(entry.getKey(), entry.getValue());
			}
		}
		appendFromMap("Species", species);
		appendFromMap("Variables", named);
		DefaultMutableTreeNode listnode = new DefaultMutableTreeNode("Lists");
		for (MutableTreeNode node : lists.values()) {
			listnode.add(node);
		}
		this.add(listnode);
		appendFromMap("Other", other);
		appendFromMap("History", appended);
		if (environment instanceof UserEnvironment) {
			UserEnvironment userenvironment = (UserEnvironment) environment;
			if (userenvironment.getLast() != null) {
				this.add(new AstNodeTreeNode("Last Result", userenvironment
						.getLast()));
			}

		}
	}
}
