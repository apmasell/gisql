package ca.wlu.gisql.interactome;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interaction.Interaction;

public class ToFile implements Interactome {

    static final Logger log = Logger.getLogger(ToFile.class);

    public static void writeInteractomeToFile(Interactome i, File file)
	    throws IOException {
	writeInteractomeToFile(i, file, 0, 1);
    }

    public static void writeInteractomeToFile(Interactome i, File file,
	    double lowerbound, double upperbound) throws IOException {
	PrintStream print = new PrintStream(file);
	writeInteractomeToFile(i, print, lowerbound, upperbound);
    }

    public static void writeInteractomeToFile(Interactome i, PrintStream print)
	    throws IOException {
	writeInteractomeToFile(i, print, 0, 1);
    }

    public static void writeInteractomeToFile(Interactome i, PrintStream print,
	    double lowerbound, double upperbound) throws IOException {

	if (lowerbound > upperbound) {
	    double temp = lowerbound;
	    lowerbound = upperbound;
	    upperbound = temp;
	}

	StringBuilder sb = new StringBuilder();
	sb.append("# ");
	print.println(i.show(sb));
	int count = 0;
	for (Interaction n : i) {
	    if (n.getMembership() >= lowerbound
		    && n.getMembership() <= upperbound) {
		sb.setLength(0);
		print.println(n.show(sb));
		count++;
	    }
	}
	sb.setLength(0);
	sb.append("# ");
	sb.append(count);
	sb.append(" interactions in ");
	sb.append(i.getComputationTime() / 1000.0);
	sb.append(" seconds.");
	print.println(sb);
	print.close();
    }

    private File file;

    private Interactome i;

    private double lowerbound = 0;

    private double upperbound = 1;

    public ToFile(Interactome i, String filename) {
	super();
	this.i = i;
	this.file = new File(filename);
    }

    public ToFile(Interactome i, String filename, double lowerbound,
	    double upperbound) {
	this(i, filename);
	this.lowerbound = lowerbound;
	this.upperbound = upperbound;
    }

    public void addTableModelListener(TableModelListener listener) {
	i.addTableModelListener(listener);
    }

    public long findOrtholog(long gene) {
	return i.findOrtholog(gene);
    }

    public Class<?> getColumnClass(int columnIndex) {
	return i.getColumnClass(columnIndex);
    }

    public int getColumnCount() {
	return i.getColumnCount();
    }

    public String getColumnName(int columnIndex) {
	return i.getColumnName(columnIndex);
    }

    public long getComputationTime() {
	return i.getComputationTime();
    }

    public Interaction getInteraction(long gene1, long gene2) {
	this.process();
	return i.getInteraction(gene1, gene2);
    }

    public int getRowCount() {
	return i.getRowCount();
    }

    public Object getValueAt(int rowIndex, int colIndex) {
	return i.getValueAt(rowIndex, colIndex);
    }

    public boolean isCellEditable(int arg0, int arg1) {
	return i.isCellEditable(arg0, arg1);
    }

    public Iterator<Interaction> iterator() {
	this.process();
	return i.iterator();
    }

    public boolean process() {
	boolean parent = i.process();
	if (parent) {
	    try {
		writeInteractomeToFile(i, file);
	    } catch (IOException e) {
		log.error("Could not write to file.", e);
	    }
	}
	return parent;
    }

    public void removeTableModelListener(TableModelListener listener) {
	i.removeTableModelListener(listener);
    }

    public void setValueAt(Object value, int rowIndex, int colIndex) {
	i.setValueAt(value, rowIndex, colIndex);

    }

    public StringBuilder show(StringBuilder sb) {
	i.show(sb);
	sb.append(" → ");
	sb.append(lowerbound).append(" ").append(upperbound).append(" ");
	sb.append("\"").append(file).append("\"");
	return sb;
    }
}
