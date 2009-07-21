package ca.wlu.gisql.gui;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;

public abstract class ComputationalTask<P extends Component & TaskParent>
		extends SwingWorker<Boolean, Interactome> {
	private static final Logger log = Logger.getLogger(ComputationalTask.class);

	private final String message;

	private final P parent;

	public ComputationalTask(P parent, String message) {
		this.parent = parent;
		this.message = ("Computing " + message + "...");
	}

	public final Boolean doInBackground() {
		try {
			return doIt();
		} catch (Exception e) {
			log.error("Mysterious error", e);
			return false;
		}
	}

	protected abstract boolean doIt();

	public final void done() {
		boolean success;
		try {
			success = get();
		} catch (Exception e) {
			success = false;
			log.warn("Unable to finish task.", e);
		}

		if (success) {
			parent.processedInteractome(getResult());
		} else {
			parent.processedInteractome(null);
			JOptionPane.showMessageDialog(parent,
					"Failed to compute result. Consult console output.",
					"gisQL", JOptionPane.ERROR_MESSAGE);
		}
	}

	public final String getMessage() {
		return message;
	}

	protected abstract CachedInteractome getResult();
}