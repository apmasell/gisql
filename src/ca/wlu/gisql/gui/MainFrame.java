package ca.wlu.gisql.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.concurrent.ExecutionException;

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
import javax.swing.JTextArea;
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
import ca.wlu.gisql.Parser;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ToFile;
import ca.wlu.gisql.interactome.ToFile.FileFormat;

public class MainFrame extends JFrame implements ActionListener, KeyListener,
		TableModelListener, TreeCellRenderer, TreeSelectionListener {

	class FileWriteTask extends SwingWorker<Boolean, Boolean> {
		private File file;

		private FileFormat format;

		private Interactome interactome;

		private double lowerbound;

		private JFrame parent;

		private double upperbound;

		FileWriteTask(JFrame parent, Interactome interactome,
				FileFormat format, File file, double lowerbound,
				double upperbound) {
			this.parent = parent;
			this.interactome = interactome;
			this.format = format;
			this.file = file;
			this.lowerbound = lowerbound;
			this.upperbound = upperbound;
		}

		public Boolean doInBackground() {
			return ToFile.write(interactome, format, file, lowerbound,
					upperbound);
		}

		public void done() {
			boolean success = false;
			try {
				success = get();
			} catch (InterruptedException e) {
				log.error("Error saving file.", e);
			} catch (ExecutionException e) {
				log.error("Error saving file.", e);
			}
			if (!success)
				JOptionPane.showMessageDialog(parent, "Error writing to file.",
						"gisQL", JOptionPane.WARNING_MESSAGE);

			progress.stop();
		}
	}

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
			progress.stop();
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

	private JTextArea helptext = new JTextArea(Parser.getHelp());

	private JScrollPane helptextpane = new JScrollPane(helptext);

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

	private JMenuItem menuSave = new JMenuItem("Save Data As...");

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
		helptext.setEditable(false);

		resulttabs.addTab("Interctions", interactionspane);
		resulttabs.addTab("Genes", genesspane);
		resulttabs.addTab("Help", helptextpane);
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
		menuSave.addActionListener(this);
		menuMain.add(menuSave);

		menuClear.addActionListener(this);
		menuMain.add(menuClear);
		menuMain.add(quitseparator);

		menuQuit.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Q,
				java.awt.event.InputEvent.CTRL_MASK));
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
			name = name.trim();
			if (name.length() == 0)
				return;

			for (int i = 0; i < name.length(); i++) {
				if (!(i == 0 ? Character.isJavaIdentifierStart(name.charAt(i))
						: Character.isJavaIdentifierPart(name.charAt(i)))) {
					JOptionPane.showMessageDialog(this, "Inavlid name.",
							"Name - gisQL", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			env.setVariable(name, env.getLast());

		} else if (evt.getSource() == menuSave) {
			if (interactions.getModel() instanceof Interactome) {
				JFileChooser fc = new JFileChooser();
				ExportAccessory ea = new ExportAccessory();
				fc.setAccessory(ea);

				if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					FileWriteTask fwt = new FileWriteTask(this,
							(Interactome) interactions.getModel(), ea
									.getFormat(), fc.getSelectedFile(), ea
									.getLowerbound(), ea.getUpperbound());
					fwt.execute();
					progress.start("Saving to "
							+ fc.getSelectedFile().getName() + "...");
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
		Parser parser = new Parser(env, expression);
		Interactome i = parser.get();
		if (i == null) {
			JOptionPane.showMessageDialog(this, parser.getErrors(),
					"Expression Error - gisQL", JOptionPane.ERROR_MESSAGE);
			return;
		}
		interactions.setModel(emptyModel);
		String expr = i.show(new StringBuilder()).toString();
		command.setText(expr);
		log.info(expr);
		env.append(i);
		task = new InteractomeTask(i);
		task.execute();
		progress.start("Computing " + i.show(new StringBuilder()).toString()
				+ "...");
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
