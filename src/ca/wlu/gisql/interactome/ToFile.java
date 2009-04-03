package ca.wlu.gisql.interactome;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import ca.wlu.gisql.interaction.Interaction;

public class ToFile extends AbstractShadowInteractome {

    public static void writeInteractomeToFile(Interactome i, File file)
	    throws IOException {
	writeInteractomeToFile(i, file, 0, 1);
    }

    public static void writeInteractomeToFile(Interactome i, File file,
	    double lowerbound, double upperbound) throws IOException {
	PrintStream print = new PrintStream(file);
	writeInteractomeToFile(i, print, lowerbound, upperbound);
	print.close();
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
		sb.append(n.getGene1()).append(", ");
		sb.append(n.getGene2()).append(", ");
		sb.append(n.getMembership()).append(", ");
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
    }

    private File file;

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

    public void postprocess() {
	try {
	    writeInteractomeToFile(i, file);
	} catch (IOException e) {
	    log.error("Could not write to file.", e);
	}
    }

    public StringBuilder show(StringBuilder sb) {
	i.show(sb);
	sb.append(" → ");
	sb.append(lowerbound).append(" ").append(upperbound).append(" ");
	sb.append("\"").append(file).append("\"");
	return sb;
    }
}
