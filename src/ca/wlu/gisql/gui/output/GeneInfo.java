package ca.wlu.gisql.gui.output;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import ca.wlu.gisql.graph.Accession;
import ca.wlu.gisql.graph.Gene;

public class GeneInfo extends JPopupMenu {
	class AccessionItem extends JMenuItem implements ActionListener {
		private static final long serialVersionUID = 1367461088010182953L;
		private final URI uri;

		public AccessionItem(final Accession accession)
				throws URISyntaxException {
			super(accession.toString());
			uri = new URI(ncbiUrl + accession.getIdentifier());
			this.addActionListener(this);
		}

		public void actionPerformed(ActionEvent event) {
			if (event.getSource() != this)
				return;

			if (!java.awt.Desktop.isDesktopSupported()) {
				GeneInfo.log.error("Desktop is not supported (fatal)");
				return;
			}

			Desktop desktop = Desktop.getDesktop();
			if (!desktop.isSupported(Desktop.Action.BROWSE)) {
				GeneInfo.log
						.error("Desktop doesn't support the browse action (fatal)");
				return;
			}

			try {
				desktop.browse(uri);
			} catch (IOException e) {
				GeneInfo.log.error("Error opening URL", e);
			}
		}
	}

	private static final Logger log = Logger.getLogger(GeneInfo.class);
	private static final String ncbiUrl = "http://www.ncbi.nlm.nih.gov/protein/";
	private static final long serialVersionUID = -8511621648918100042L;

	public GeneInfo(final Gene gene) {
		super();
		add(gene, false);
	}

	private void add(Gene gene, boolean seperator) {
		if (seperator)
			this.addSeparator();
		for (Accession accession : gene) {
			try {
				this.add(new AccessionItem(accession));
			} catch (URISyntaxException e) {
				log.warn("Skipping accession due to URL error.", e);
			}
		}
	}

	public GeneInfo append(Gene gene) {
		add(gene, true);
		return this;
	}

}
