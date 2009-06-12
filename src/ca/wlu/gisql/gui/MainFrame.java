package ca.wlu.gisql.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.apache.log4j.Logger;

import ca.wlu.gisql.environment.EnvironmentUtils;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.gui.output.EnvironmentTreeView;
import ca.wlu.gisql.gui.output.InteractomeTreeCellRender;
import ca.wlu.gisql.gui.output.ResultTab;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;

public class MainFrame extends JFrame implements ActionListener, TaskParent,
		TreeSelectionListener {

	private static final Logger log = Logger.getLogger(MainFrame.class);

	private static final long serialVersionUID = -1767901719339978452L;

	private final UserEnvironment environment;

	private final EnvironmentTreeView environmentTree;

	private final JSplitPane innersplitpane = new JSplitPane();

	private final JMenuBar menu = new JMenuBar();

	private final JMenuItem menuClear = new JMenuItem("Clear Variables");

	private final JMenu menuMain = new JMenu("Main");

	private final JMenuItem menuName = new JMenuItem(
			"Assign Last Result to Variable...");

	private final JMenuItem menuQuit = new JMenuItem("Quit");

	private final JMenuItem menuSave = new JMenuItem("Save Data As...");

	final BusyDialog progress = new BusyDialog(this);

	private final JSeparator quitseparator = new JSeparator();

	private final ResultTab results = new ResultTab();

	private final CommandBox command;

	private InteractomeTask<MainFrame> task = null;

	private final JTree variablelist = new JTree();

	private final JScrollPane variablelistPane = new JScrollPane(variablelist);

	public MainFrame(UserEnvironment environment) {
		super("gisQL");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.environment = environment;

		command = new CommandBox(environment);
		command.setActionListener(this);

		environmentTree = new EnvironmentTreeView(environment);
		variablelist.setModel(environmentTree);
		variablelist.addTreeSelectionListener(this);
		ToolTipManager.sharedInstance().registerComponent(variablelist);
		variablelist.setCellRenderer(new InteractomeTreeCellRender(
				environmentTree));

		innersplitpane.setRightComponent(variablelistPane);
		innersplitpane.setLeftComponent(results);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(command, BorderLayout.NORTH);
		getContentPane().add(results.getStatusBar(), BorderLayout.SOUTH);
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
		if (evt.getSource() == command) {
			Interactome interactome = command.getInteractome();
			if (interactome == null) {
				return;
			}

			results.setInteractome(null);
			task = new InteractomeTask<MainFrame>(this, environment
					.append(interactome));
			task.execute();
			progress.start(task.getMessage());
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
					JOptionPane.showMessageDialog(this, "Invalid name.",
							"Name - gisQL", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			environment.setVariable(name, environment.getLast());

		} else if (evt.getSource() == menuSave) {
			if (results.getInteractome() != null) {
				JFileChooser fc = new JFileChooser();
				ExportAccessory ea = new ExportAccessory();
				fc.setAccessory(ea);

				if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					FileWriteTask fwt = new FileWriteTask(this, results
							.getInteractome(), ea.getFormat(), fc
							.getSelectedFile(), ea.getLowerbound(), ea
							.getUpperbound());
					fwt.execute();
					progress.start("Saving to "
							+ fc.getSelectedFile().getName() + "...");
				}
			}
		} else if (evt.getSource() == menuClear) {
			EnvironmentUtils.clear(environment);
		} else if (evt.getSource() == menuQuit) {
			System.exit(0);
		}
	}

	public void valueChanged(TreeSelectionEvent evt) {
		try {
			CachedInteractome i = CachedInteractome.wrap(environmentTree
					.getInteractome(variablelist.getSelectionPath()), null);
			if (i != null) {
				results.setInteractome(i);
				i.process(); /* Probably processed. */
			}
		} catch (Exception e) {
			log.error("Error picking interactome from list.", e);
		}
	}

	public void processedInteractome(CachedInteractome interactome) {
		results.setInteractome(interactome);
		command.clearCommand();
		progress.stop();
		task = null;
	}
}
