package ca.wlu.gisql.gui.output;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.gui.FilterBox;
import ca.wlu.gisql.interactome.CachedInteractome;

import com.sun.forums.filteringtable.FilteringTableModel;

public class ResultTab extends JTabbedPane implements TableModelListener {

	private static final TableModel emptyModel = new DefaultTableModel();

	private static final long serialVersionUID = -5429564862842971330L;

	private final FilterBox filter = new FilterBox();

	private final JTable genes = new JTable();

	private final JLabel geneslabel = new JLabel(" genes.");

	private final JLabel genesRowLabel = new JLabel("No");

	private final JScrollPane genesspane = new JScrollPane(genes);

	private final JTextArea helptext;

	private final JTable interactions = new JTable();

	private final JLabel interactionslabel = new JLabel(" interactions. ");

	private final JScrollPane interactionspane = new JScrollPane(interactions);

	private final JLabel interactionsRowLabel = new JLabel("No");

	private CachedInteractome interactome = null;

	private final JLabel status = new JLabel();

	private final JToolBar statusbar = new JToolBar();

	private final Separator statusseparator = new Separator();

	public ResultTab(Environment environment) {
		super();

		interactions.setAutoCreateRowSorter(true);
		genes.setAutoCreateRowSorter(true);
		helptext = new JTextArea(environment.getParserKb().getHelp());
		helptext.setEditable(false);

		addTab("Interactions", interactionspane);
		addTab("Genes", genesspane);
		addTab("Help", new JScrollPane(helptext));

		statusbar.setFloatable(false);
		statusbar.setRollover(true);
		statusbar.add(interactionsRowLabel);
		statusbar.add(interactionslabel);
		statusbar.add(genesRowLabel);
		statusbar.add(geneslabel);
		statusbar.add(statusseparator);
		statusbar.add(status);
	}

	public CachedInteractome getInteractome() {
		return interactome;
	}

	public JToolBar getStatusBar() {
		return statusbar;
	}

	public JToolBar getToolBar() {
		return filter;
	}

	public void setInteractome(CachedInteractome interactome) {
		for (JTable table : new JTable[] { interactions, genes }) {
			TableModel old = table.getModel();
			if (old != null) {
				old.removeTableModelListener(this);
			}
		}
		if (interactome != null) {
			genes.removeMouseListener(interactome.getGeneTable());
			genes.getSelectionModel().removeListSelectionListener(
					interactome.getGeneTable());
			interactions.removeMouseListener(interactome.getInteractionTable());
			interactions.getSelectionModel().removeListSelectionListener(
					interactome.getInteractionTable());
		}

		this.interactome = interactome;

		if (interactome == null) {
			interactions.setModel(emptyModel);
			genes.setModel(emptyModel);
		} else {
			FilteringTableModel interactionModel = new FilteringTableModel(
					interactome.getInteractionTable());
			interactionModel.addFilter(filter);
			interactions.setModel(interactionModel);

			FilteringTableModel geneModel = new FilteringTableModel(interactome
					.getGeneTable());
			geneModel.addFilter(filter);
			genes.setModel(geneModel);

			genes.addMouseListener(interactome.getGeneTable());
			genes.getSelectionModel().addListSelectionListener(
					interactome.getGeneTable());
			interactions.addMouseListener(interactome.getInteractionTable());
			interactions.getSelectionModel().addListSelectionListener(
					interactome.getInteractionTable());

			interactome.getInteractionTable().addTableModelListener(this);
			interactome.getGeneTable().addTableModelListener(this);
			tableChanged(null);
		}
	}

	public void tableChanged(TableModelEvent evt) {
		interactionsRowLabel.setText(Integer.toString(interactions
				.getRowCount()));
		genesRowLabel.setText(Integer.toString(genes.getRowCount()));
	}

}
