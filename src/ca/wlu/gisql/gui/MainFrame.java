package ca.wlu.gisql.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.JToolBar.Separator;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import ca.wlu.gisql.Environment;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ToFile;

public class MainFrame extends JFrame implements ActionListener, KeyListener,
	TableModelListener, TreeCellRenderer, TreeSelectionListener {

    class InteractomeTask extends SwingWorker<Interactome, Interactome> {

	private Interactome i;

	InteractomeTask(Interactome i) {
	    this.i = i;
	}

	public Interactome doInBackground() {
	    i.process();
	    return i;
	}

	public void done() {
	    setInteractome(i);
	    command.setText("");
	    progress.setVisible(false);
	    task = null;
	}
    }

    static final TableModel emptyModel = new DefaultTableModel();

    static final Logger log = Logger.getLogger(MainFrame.class);

    private static final long serialVersionUID = -1767901719339978452L;

    private JTextField command = new JTextField();

    private Environment env;

    private JTable genes = new JTable();

    private JLabel geneslabel = new JLabel(" genes.");

    private JLabel genesRowLabel = new JLabel("No");

    private JScrollPane genesspane = new JScrollPane(genes);

    private JSplitPane innersplitpane = new JSplitPane();

    private JTable interactions = new JTable();

    private JLabel interactionslabel = new JLabel(" interactions. ");

    private JScrollPane interactionspane = new JScrollPane(interactions);

    private JLabel interactionsRowLabel = new JLabel("No");

    private JMenuBar menu = new JMenuBar();

    private JMenuItem menuClear = new JMenuItem("Clear Variables");

    private JMenu menuMain = new JMenu("Main");

    private JMenuItem menuName = new JMenuItem(
	    "Assign Last Result to Variable...");

    private JMenuItem menuQuit = new JMenuItem("Quit");

    private JMenuItem menuSave = new JMenuItem("Save Results As...");

    private int numCommands = 1;

    private BusyDialog progress = new BusyDialog(this);

    private JSeparator quitseparator = new JSeparator();

    private JTabbedPane resulttabs = new JTabbedPane();

    private javax.swing.JButton run = new JButton("Run");

    private JLabel status = new JLabel();

    private JToolBar statusbar = new JToolBar();

    private Separator statusseparator = new Separator();

    private InteractomeTask task = null;

    private JToolBar toolbar = new JToolBar();

    private Separator toolbarSeperator = new Separator();

    private DefaultTreeCellRenderer treerenderer = new DefaultTreeCellRenderer();

    private JTree variablelist = new JTree();

    private JScrollPane variablelistPane = new JScrollPane(variablelist);

    public MainFrame(Environment env) {
	super("gisQL");
	this.env = env;

	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	getContentPane().setLayout(new BorderLayout());

	toolbar.setFloatable(false);
	toolbar.setRollover(true);

	toolbar.add(new JLabel("Query: "));
	command.addKeyListener(this);
	toolbar.add(command);
	toolbar.add(toolbarSeperator);

	run.setFocusable(false);
	run.addActionListener(this);
	toolbar.add(run);
	getContentPane().add(toolbar, BorderLayout.NORTH);

	interactions.setAutoCreateRowSorter(true);
	genes.setAutoCreateRowSorter(true);
	resulttabs.addTab("Interctions", interactionspane);
	resulttabs.addTab("Genes", genesspane);
	innersplitpane.setLeftComponent(resulttabs);

	statusbar.setFloatable(false);
	statusbar.setRollover(true);
	statusbar.add(interactionsRowLabel);
	statusbar.add(interactionslabel);
	statusbar.add(genesRowLabel);
	statusbar.add(geneslabel);
	statusbar.add(statusseparator);
	statusbar.add(status);
	getContentPane().add(statusbar, BorderLayout.SOUTH);

	variablelist.setModel(env);
	variablelist.addTreeSelectionListener(this);
	ToolTipManager.sharedInstance().registerComponent(variablelist);
	variablelist.setCellRenderer(this);
	innersplitpane.setRightComponent(variablelistPane);
	getContentPane().add(innersplitpane, BorderLayout.CENTER);

	menuName.setAccelerator(KeyStroke.getKeyStroke(
		java.awt.event.KeyEvent.VK_N,
		java.awt.event.InputEvent.CTRL_MASK));
	menuName.addActionListener(this);
	menuMain.add(menuName);

	menuSave.setAccelerator(KeyStroke.getKeyStroke(
		java.awt.event.KeyEvent.VK_S,
		java.awt.event.InputEvent.CTRL_MASK));
	menuSave.setText("Save Data As...");
	menuSave.addActionListener(this);
	menuMain.add(menuSave);

	menuClear.setText("Clear Variables");
	menuClear.addActionListener(this);
	menuMain.add(menuClear);
	menuMain.add(quitseparator);

	menuQuit.setAccelerator(KeyStroke.getKeyStroke(
		java.awt.event.KeyEvent.VK_Q,
		java.awt.event.InputEvent.CTRL_MASK));
	menuQuit.setText("Quit");
	menuQuit.addActionListener(this);
	menuMain.add(menuQuit);

	menu.add(menuMain);

	setJMenuBar(menu);

	pack();
    }

    public void actionPerformed(ActionEvent evt) {
	if (evt.getSource() == run) {
	    executeCurrentCommand();
	} else if (evt.getSource() == menuName) {

	    String name = JOptionPane.showInputDialog(this,
		    "Enter a variable name:");
	    if (name == null) {
		return;
	    }
	    for (int i = 0; i < name.length(); i++) {
		if (!Character.isJavaIdentifierPart(name.charAt(i))) {
		    JOptionPane.showMessageDialog(this, "Inavlid name.",
			    "Name - gisQL", JOptionPane.ERROR_MESSAGE);
		    return;
		}
	    }
	    env.setVariable(name, env.getLast());

	} else if (evt.getSource() == menuSave) {
	    if (interactions.getModel() instanceof Interactome) {
		JFileChooser fc = new JFileChooser();
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
		    try {

			ToFile.writeInteractomeToFile(
				(Interactome) interactions.getModel(), fc
					.getSelectedFile());
		    } catch (IOException e) {
			log.error("Could not write to file.", e);
			JOptionPane.showMessageDialog(this,
				"Error writing to file.", "gisQL",
				JOptionPane.WARNING_MESSAGE);
		    }
		}
	    }
	} else if (evt.getSource() == menuClear) {
	    env.clearVariables();
	} else if (evt.getSource() == menuQuit) {
	    System.exit(0);
	}
    }

    public void executeCurrentCommand() {
	String expression = command.getText();
	if (expression == null || expression.trim().length() == 0) {
	    return;
	}
	Interactome i = env.parse(expression);
	if (i == null) {
	    JOptionPane.showMessageDialog(this, "Invalid expression.", "gisQL",
		    JOptionPane.ERROR_MESSAGE);
	    return;
	}
	interactions.setModel(emptyModel);
	String expr = i.show(new StringBuilder()).toString();
	command.setText(expr);
	log.info(expr);
	env.setVariable("_" + numCommands++, i);
	task = new InteractomeTask(i);
	task.execute();
	progress.setVisible(true);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
	    boolean selected, boolean expanded, boolean leaf, int row,
	    boolean hasFocus) {
	treerenderer.getTreeCellRendererComponent(tree, value, selected,
		expanded, leaf, row, hasFocus);
	if (value != null) {
	    TreePath tp = tree.getPathForRow(row);
	    Interactome i = env.getInteractome(tp);
	    if (i != null) {
		treerenderer.setToolTipText(i.show(new StringBuilder())
			.toString());
	    } else {
		treerenderer.setToolTipText(null);
	    }
	}
	return treerenderer;
    }

    public void keyPressed(KeyEvent evt) {
	if (evt.getSource() == command && evt.getKeyCode() == KeyEvent.VK_ENTER) {
	    executeCurrentCommand();
	}
    }

    public void keyReleased(KeyEvent evt) {
    }

    public void keyTyped(KeyEvent evt) {
    }

    private void setInteractome(Interactome i) {
	for (JTable table : new JTable[] { interactions, genes }) {
	    TableModel old = table.getModel();
	    if (old != null) {
		old.removeTableModelListener(this);
	    }
	}
	interactions.setModel(i);
	genes.setModel(i.genes());
	if (i != null) {
	    i.addTableModelListener(this);
	    i.genes().addTableModelListener(this);
	    tableChanged(null);
	}
    }

    public void tableChanged(TableModelEvent evt) {
	interactionsRowLabel.setText(Integer.toString(interactions
		.getRowCount()));
	genesRowLabel.setText(Integer.toString(genes.getRowCount()));
    }

    public void valueChanged(TreeSelectionEvent evt) {
	Interactome i = env.getInteractome(variablelist.getSelectionPath());
	if (i != null) {
	    setInteractome(i);
	    status.setText(i.show(new StringBuilder()).toString());
	}
    }
}
