package ca.wlu.gisql.gui;

import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;

class InteractomeTask extends SwingWorker<Boolean, Interactome> {

	private final MainFrame frame;

	private final CachedInteractome i;

	InteractomeTask(MainFrame frame, CachedInteractome i) {
		this.frame = frame;
		this.i = i;
	}

	public Boolean doInBackground() {
		try {
			i.process();
			return true;
		} catch (Exception e) {
			MainFrame.log.error("Mysterious error", e);
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
			this.frame.setInteractome(i);
			this.frame.command.setText("");
		} else {
			JOptionPane.showMessageDialog(frame,
					"Failed to compute result. Consult console output.",
					"gisQL", JOptionPane.ERROR_MESSAGE);
		}

		this.frame.progress.stop();
		this.frame.task = null;
	}
}