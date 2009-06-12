package ca.wlu.gisql.gui;

import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.output.AbstractOutput;
import ca.wlu.gisql.interactome.output.FileFormat;

class FileWriteTask extends SwingWorker<Boolean, Boolean> {
	private static final Logger log = Logger.getLogger(FileWriteTask.class);

	private final File file;

	private final FileFormat format;

	private final MainFrame frame;

	private final Interactome interactome;

	private final double lowerbound;

	private final double upperbound;

	FileWriteTask(MainFrame frame, Interactome interactome, FileFormat format,
			File file, double lowerbound, double upperbound) {
		this.frame = frame;
		this.interactome = interactome;
		this.format = format;
		this.file = file;
		this.lowerbound = lowerbound;
		this.upperbound = upperbound;
	}

	public Boolean doInBackground() {
		return AbstractOutput.wrap(interactome, null, lowerbound, upperbound,
				format, file.getPath(), true).process();
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
			JOptionPane.showMessageDialog(frame, "Error writing to file.",
					"gisQL", JOptionPane.WARNING_MESSAGE);

		this.frame.progress.stop();
	}
}