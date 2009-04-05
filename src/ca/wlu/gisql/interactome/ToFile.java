package ca.wlu.gisql.interactome;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;

import javax.xml.transform.TransformerConfigurationException;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.GmlExporter;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.ext.MatrixExporter;
import org.jgrapht.graph.UndirectedMaskSubgraph;
import org.xml.sax.SAXException;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.gui.GeneIdProvider;
import ca.wlu.gisql.gui.GeneNameProvider;
import ca.wlu.gisql.gui.InteractionIdProvider;
import ca.wlu.gisql.gui.InteractionNameProvider;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.LimitedMembershipFunctor;

public class ToFile extends AbstractShadowInteractome {

    public static final int FORMAT_GENOME_TEXT = 1;

    public static final int FORMAT_INTERACTOME_DOT = 2;

    public static final int FORMAT_INTERACTOME_GML = 3;

    public static final int FORMAT_INTERACTOME_GRAPHML = 4;

    public static final int FORMAT_INTERACTOME_MATRIX_ADJACENCY = 5;

    public static final int FORMAT_INTERACTOME_MATRIX_LAPLACE = 6;

    public static final int FORMAT_INTERACTOME_TEXT = 0;

    private static PrintStream printFooter(PrintStream print, int count,
	    Interactome i) {
	print.print("# ");
	print.print(count);
	print.print(" records in ");
	print.print(i.getComputationTime() / 1000.0);
	print.print(" seconds.");
	print.println();
	return print;
    }

    private static int printGenes(Interactome i, PrintStream print,
	    double lowerbound, double upperbound) throws IOException {
	int count = 0;
	for (Gene g : i.genes()) {
	    if (g.getMembership() >= lowerbound
		    && g.getMembership() <= upperbound) {
		print.print(g.getId());
		print.print(", ");
		print.print(g.getMembership());
		print.print(", ");
		g.show(print);
		print.println();
		count++;
	    }
	}
	return count;
    }

    private static PrintStream printHeader(PrintStream print, Interactome i)
	    throws IOException {
	print.print("# ");
	i.show(print);
	print.println();
	return print;
    }

    private static int printInteractions(Interactome i, PrintStream print,
	    double lowerbound, double upperbound) throws IOException {

	int count = 0;
	for (Interaction n : i) {
	    if (n.getMembership() >= lowerbound
		    && n.getMembership() <= upperbound) {
		print.print(n.getGene1());
		print.print(", ");
		print.print(n.getGene2());
		print.print(", ");
		print.print(n.getMembership());
		print.print(", ");
		n.show(print);
		print.println();
		count++;
	    }
	}
	return count;
    }

    public static boolean write(Interactome i, int format, File file,
	    double lowerbound, double upperbound) {
	try {
	    UndirectedMaskSubgraph<Gene, Interaction> subgraph = new UndirectedMaskSubgraph<Gene, Interaction>(
		    i, new LimitedMembershipFunctor(lowerbound, upperbound));
	    Writer writer;

	    switch (format) {
	    case FORMAT_INTERACTOME_DOT:
		writer = new FileWriter(file);
		DOTExporter<Gene, Interaction> dotexporter = new DOTExporter<Gene, Interaction>(
			new GeneIdProvider(), new GeneNameProvider(),
			new InteractionNameProvider());

		dotexporter.export(writer, subgraph);
		writer.close();
		return true;

	    case FORMAT_INTERACTOME_GML:
		writer = new FileWriter(file);
		GmlExporter<Gene, Interaction> gmlexporter = new GmlExporter<Gene, Interaction>();

		gmlexporter.export(writer, subgraph);
		writer.close();
		return true;

	    case FORMAT_INTERACTOME_GRAPHML:
		writer = new FileWriter(file);
		GraphMLExporter<Gene, Interaction> graphmlexporter = new GraphMLExporter<Gene, Interaction>(
			new GeneIdProvider(), new GeneNameProvider(),
			new InteractionIdProvider(),
			new InteractionNameProvider());

		graphmlexporter.export(writer, subgraph);
		writer.close();
		return true;

	    case FORMAT_INTERACTOME_MATRIX_ADJACENCY:
	    case FORMAT_INTERACTOME_MATRIX_LAPLACE:
		writer = new FileWriter(file);
		MatrixExporter<Gene, Interaction> matrixexporter = new MatrixExporter<Gene, Interaction>();
		if (format == FORMAT_INTERACTOME_MATRIX_ADJACENCY) {
		    matrixexporter.exportAdjacencyMatrix(writer, subgraph);
		} else {
		    matrixexporter.exportLaplacianMatrix(writer, subgraph);
		}
		writer.close();
		return true;
	    default:
		PrintStream print = new PrintStream(file);
		boolean result = write(i, format, print, lowerbound, upperbound);
		print.close();
		return result;
	    }
	} catch (IOException e) {
	    log.error("Could not write to file.", e);
	} catch (TransformerConfigurationException e) {
	    log.error("Could not write to file.", e);
	} catch (SAXException e) {
	    log.error("SAX error writing to file.", e);
	}
	return false;
    }

    public static boolean write(Interactome i, int format, PrintStream print,
	    double lowerbound, double upperbound) {
	int count;
	try {
	    switch (format) {
	    case FORMAT_INTERACTOME_TEXT:
		printHeader(print, i);
		count = printInteractions(i, print, lowerbound, upperbound);
		printFooter(print, count, i);
		return true;
	    case FORMAT_GENOME_TEXT:
		printHeader(print, i);
		count = printGenes(i, print, lowerbound, upperbound);
		printFooter(print, count, i);
		return true;
	    default:
		return false;
	    }
	} catch (IOException e) {
	    log.error("Could not write to file.", e);
	    return false;
	}
    }

    private File file;

    private int format;

    private double lowerbound;

    private double upperbound;

    public ToFile(Interactome i, int format, String filename,
	    double lowerbound, double upperbound) {
	super();
	this.i = i;
	this.format = format;
	this.file = new File(filename);
	this.lowerbound = lowerbound;
	this.upperbound = upperbound;
    }

    public void postprocess() {
	write(i, format, file, lowerbound, upperbound);
    }

    public PrintStream show(PrintStream print) {
	i.show(print);
	print.print(" → ");
	print.print(lowerbound);
	print.print(" ");
	print.print(upperbound);
	print.print(" ");
	print.print("\"");
	print.print(file);
	print.print("\"");
	return print;
    }

    public StringBuilder show(StringBuilder sb) {
	i.show(sb);
	sb.append(" → ");
	sb.append(lowerbound).append(" ").append(upperbound).append(" ");
	sb.append("\"").append(file).append("\"");
	return sb;
    }
}
