package ca.wlu.gisql.interactome;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interaction.Interaction;

public class ToFile implements Interactome {

    private Interactome i;

    private File file;

    public ToFile(Interactome i, String filename) {
	super();
	this.i = i;
	this.file = new File(filename);
    }

    public long findOrtholog(long gene) {
	return i.findOrtholog(gene);
    }

    public static void writeInteractomeToFile(Interactome i, File file)
	    throws IOException {
	PrintStream print = new PrintStream(file);
	writeInteractomeToFile(i, print);
    }

    public static void writeInteractomeToFile(Interactome i, PrintStream print)
	    throws IOException {
	StringBuilder sb = new StringBuilder();
	sb.append("# ");
	print.println(i.show(sb));
	int count = 0;
	for (Interaction n : i) {
	    sb.setLength(0);
	    print.println(n.show(sb));
	    count++;
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

    public StringBuilder show(StringBuilder sb) {
	i.show(sb);
	sb.append(" → \"");
	sb.append(file);
	sb.append("\"");
	return sb;
    }

    public long getComputationTime() {
	return i.getComputationTime();
    }

    public Interaction getInteraction(long gene1, long gene2) {
	this.process();
	return i.getInteraction(gene1, gene2);
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

    public Iterator<Interaction> iterator() {
	this.process();
	return i.iterator();
    }

    public void addTableModelListener(TableModelListener listener) {
	i.addTableModelListener(listener);
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

    public int getRowCount() {
	return i.getRowCount();
    }

    public Object getValueAt(int rowIndex, int colIndex) {
	return i.getValueAt(rowIndex, colIndex);
    }

    public boolean isCellEditable(int arg0, int arg1) {
	return i.isCellEditable(arg0, arg1);
    }

    public void removeTableModelListener(TableModelListener listener) {
	i.removeTableModelListener(listener);
    }

    public void setValueAt(Object value, int rowIndex, int colIndex) {
	i.setValueAt(value, rowIndex, colIndex);

    }

    static final Logger log = Logger.getLogger(ToFile.class);

}
