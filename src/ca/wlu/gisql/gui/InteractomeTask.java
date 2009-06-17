package ca.wlu.gisql.gui;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class InteractomeTask<P extends Component & TaskParent> extends
		SwingWorker<Boolean, Interactome> {
	private static final Logger log = Logger.getLogger(InteractomeTask.class);

	private final CachedInteractome interactome;

	private final String message;

	private final P parent;

	public InteractomeTask(P parent, CachedInteractome interactome) {
		this.parent = parent;
		this.interactome = interactome;
		ShowableStringBuilder print = new ShowableStringBuilder();
		print.append("Computing ");
		print.print(interactome);
		print.append("...");
		message = print.toString();
	}

	public Boolean doInBackground() {
		try {
			interactome.process();
			return true;
		} catch (Exception e) {
			log.error("Mysterious error", e);
			return false;
		}
	}

	public void done() {
		boolean success;
		try {
			success = get();
		} catch (Exception e) {
			success = false;
			log.warn("Unable to finish task.", e);
		}

		if (success) {
			parent.processedInteractome(interactome);
		} else {
			parent.processedInteractome(null);
			JOptionPane.showMessageDialog(parent,
					"Failed to compute result. Consult console output.",
					"gisQL", JOptionPane.ERROR_MESSAGE);
		}
	}

	public String getMessage() {
		return message;
	}
}