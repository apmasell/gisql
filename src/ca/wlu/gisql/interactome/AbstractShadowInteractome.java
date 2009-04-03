package ca.wlu.gisql.interactome;

import java.util.Iterator;

import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.GeneSet;

public abstract class AbstractShadowInteractome implements Interactome {

    static final Logger log = Logger.getLogger(ToFile.class);

    protected Interactome i;

    public void addTableModelListener(TableModelListener listener) {
	i.addTableModelListener(listener);
    }

    public Gene findOrtholog(Gene gene) {
	return i.findOrtholog(gene);
    }

    public GeneSet genes() {
	return i.genes();
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

    public Interaction getInteraction(Gene gene1, Gene gene2) {
	this.process();
	return i.getInteraction(gene1, gene2);
    }

    public int getRowCount() {
	return i.getRowCount();
    }

    public Object getValueAt(int rowIndex, int colIndex) {
	return i.getValueAt(rowIndex, colIndex);
    }

    public double hasGene(Gene gene) {
	return i.hasGene(gene);
    }

    public boolean isCellEditable(int arg0, int arg1) {
	return i.isCellEditable(arg0, arg1);
    }

    public Iterator<Interaction> iterator() {
	this.process();
	return i.iterator();
    }

    public int numGenomes() {
	return i.numGenomes();
    }

    protected abstract void postprocess();

    public boolean process() {
	boolean parent = i.process();
	if (parent) {
	    postprocess();
	}
	return parent;
    }

    public void removeTableModelListener(TableModelListener listener) {
	i.removeTableModelListener(listener);
    }

    public void setValueAt(Object value, int rowIndex, int colIndex) {
	i.setValueAt(value, rowIndex, colIndex);

    }

}
