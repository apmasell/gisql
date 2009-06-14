package ca.wlu.gisql.gui;

import java.awt.Component;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;

public class InteractomeTask<P extends Component & TaskParent> extends
		SwingWorker<Boolean, Interactome> {
	private static final Logger log = Logger.getLogger(InteractomeTask.class);

	private final CachedInteractome interactome;

	private String message;

	private final P parent;

	public InteractomeTask(P parent, CachedInteractome interactome) {
		this.parent = parent;
		this.interactome = interactome;
		StringBuilder sb = new StringBuilder();
		sb.append("Computing ");
		interactome.show(sb);
		sb.append("...");
		message = sb.toString();
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
		} catch (InterruptedException e) {
			success = false;
		} catch (ExecutionException e) {
			success = false;
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