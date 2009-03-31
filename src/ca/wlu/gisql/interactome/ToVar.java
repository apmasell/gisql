package ca.wlu.gisql.interactome;

import java.util.Iterator;

import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

import ca.wlu.gisql.Environment;
import ca.wlu.gisql.interaction.Interaction;

public class ToVar implements Interactome {
    static final Logger log = Logger.getLogger(ToFile.class);

    private Environment env;

    private Interactome i;

    private String varname;

    public ToVar(Environment env, Interactome i, String varname) {
	super();
	this.env = env;
	this.i = i;
	this.varname = varname;
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
	    env.setVariable(varname, i);
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
	sb.append(" ≝ ");
	sb.append(varname);
	return sb;
    }
}
