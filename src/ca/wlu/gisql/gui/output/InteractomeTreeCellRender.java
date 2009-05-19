package ca.wlu.gisql.gui.output;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import ca.wlu.gisql.interactome.Interactome;

public class InteractomeTreeCellRender implements TreeCellRenderer {
	private EnvironmentTreeView environmentTree;

	private DefaultTreeCellRenderer treerenderer = new DefaultTreeCellRenderer();

	public InteractomeTreeCellRender(EnvironmentTreeView environmentTree) {
		super();
		this.environmentTree = environmentTree;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		treerenderer.getTreeCellRendererComponent(tree, value, selected,
				expanded, leaf, row, hasFocus);
		if (value != null) {
			TreePath tp = tree.getPathForRow(row);
			Interactome i = environmentTree.getInteractome(tp);
			if (i != null) {
				treerenderer.setToolTipText(i.show(new StringBuilder())
						.toString());
			} else {
				treerenderer.setToolTipText(null);
			}
		}
		return treerenderer;
	}

}
