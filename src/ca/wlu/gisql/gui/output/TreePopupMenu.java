package ca.wlu.gisql.gui.output;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.gui.CommandBox;

public class TreePopupMenu extends JPopupMenu implements ActionListener {
	public interface OpenInteractomeListener {
		public void openInteractome(AstNode node);
	}

	private static final long serialVersionUID = -2602317528135056051L;

	private JMenuItem append = new JMenuItem("Append to Query");
	private final CommandBox command;
	private JMenuItem open = new JMenuItem("View");
	private final OpenInteractomeListener parent;
	private AstNode selection = null;

	public TreePopupMenu(CommandBox command, OpenInteractomeListener parent) {
		super();
		this.command = command;
		this.parent = parent;
		this.add(open);
		this.add(append);
		open.addActionListener(this);
		append.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (selection != null) {
			if (e.getSource() == open && parent != null) {
				parent.openInteractome(selection);
			} else if (e.getSource() == append) {
				command.appendText(selection.toString());
			}
		}
	}

	public void show(Component invoker, int x, int y, AstNode node) {
		if (node != null) {
			selection = node;
			this.show(invoker, x, y);
		}

	}
}
