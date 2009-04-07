package ca.wlu.gisql.interactome;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.GmlExporter;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.ext.MatrixExporter;
import org.jgrapht.graph.UndirectedMaskSubgraph;
import org.xml.sax.SAXException;

import ca.wlu.gisql.Environment;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.gui.GeneIdProvider;
import ca.wlu.gisql.gui.GeneNameProvider;
import ca.wlu.gisql.gui.InteractionIdProvider;
import ca.wlu.gisql.gui.InteractionNameProvider;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.LimitedMembershipFunctor;
import ca.wlu.gisql.util.Parseable;
import ca.wlu.gisql.util.Statistics;

public class ToFile extends AbstractShadowInteractome {

    public enum FileFormat {
	genome("Genome Table"), interactome("Interactome Table"), dot(
		"Dot Graph"), gml("GML File"), graphml("GraphML File"), adjacency(
		"MatLab Adjacency Matrix"), laplace("MatLab Laplace Matrix"), summary(
		"Summary Statistics Only");
	private String description;

	FileFormat(String description) {
	    this.description = description;
	}

	public String toString() {
	    return description;
	}

    }

    public static final Parseable descriptor = new Parseable() {

	public Interactome construct(Environment environment,
		List<Object> params) {
	    Interactome interactome = (Interactome) params.get(0);
	    Double lowerbound = (Double) params.get(1);
	    Double upperbound = (Double) params.get(2);
	    String formatname = (String) params.get(3);
	    FileFormat format = (formatname == null ? FileFormat.interactome
		    : FileFormat.valueOf(formatname));
	    String filename = (String) params.get(4);

	    if (format == null) {
		format = FileFormat.interactome;
	    }

	    if (upperbound == null) {
		if (lowerbound == null) {
		    upperbound = 1.0;
		} else {
		    upperbound = lowerbound;
		}
		lowerbound = 0.0;
	    }

	    return new ToFile(interactome, format, filename, lowerbound,
		    upperbound);
	}

	public int getNestingLevel() {
	    return 0;
	}

	public boolean isMatchingOperator(char c) {
	    return c == '@';
	}

	public boolean isPrefixed() {
	    return false;
	}

	public PrintStream show(PrintStream print) {
	    print
		    .print("Write to file\tA @ [lowerbound [upperbound]] [{summary|interactome|genome|dot|gml|graphml|adjacency|laplace}] \"filename\"");
	    return print;
	}

	public StringBuilder show(StringBuilder sb) {
	    sb
		    .append("Write to file\tA @ [lowerbound [upperbound]] [{summary|interactome|genome|dot|gml|graphml|adjacency|laplace}] \"filename\"");
	    return sb;
	}

	public NextTask[] tasks() {
	    return new NextTask[] { NextTask.Maybe, NextTask.Double,
		    NextTask.Maybe, NextTask.Double, NextTask.Maybe,
		    NextTask.Name, NextTask.QuotedString };
	}

    };

    private static final int STANDARD_BIN_COUNT = 10;

    private static PrintStream printFooter(PrintStream print, Statistics stats,
	    Interactome interactome) {
	stats.show(print);
	print.println();
	print.print("# Computation time: ");
	print.print(interactome.getComputationTime() / 1000.0);
	print.print(" seconds.");
	print.println();
	return print;
    }

    private static Statistics printGenes(Interactome interactome,
	    PrintStream print, double lowerbound, double upperbound)
	    throws IOException {
	Statistics stats = new Statistics(STANDARD_BIN_COUNT, lowerbound,
		upperbound);
	for (Gene gene : interactome.genes()) {
	    if (gene.getMembership() >= lowerbound
		    && gene.getMembership() <= upperbound) {
		print.print(gene.getId());
		print.print(", ");
		print.print(gene.getMembership());
		print.print(", ");
		gene.show(print);
		print.println();
		stats.count(gene);
	    }
	}
	return stats;
    }

    private static PrintStream printHeader(PrintStream print,
	    Interactome interactome) throws IOException {
	print.print("# ");
	interactome.show(print);
	print.println();
	return print;
    }

    private static Statistics printInteractions(Interactome interactome,
	    PrintStream print, double lowerbound, double upperbound)
	    throws IOException {

	Statistics stats = new Statistics(STANDARD_BIN_COUNT, lowerbound,
		upperbound);
	for (Interaction interaction : interactome) {
	    if (interaction.getMembership() >= lowerbound
		    && interaction.getMembership() <= upperbound) {
		print.print(interaction.getGene1());
		print.print(", ");
		print.print(interaction.getGene2());
		print.print(", ");
		print.print(interaction.getMembership());
		print.print(", ");
		interaction.show(print);
		print.println();
		stats.count(interaction);
	    }
	}
	return stats;
    }

    public static boolean write(Interactome interactome, FileFormat format,
	    File file, double lowerbound, double upperbound) {
	try {
	    UndirectedMaskSubgraph<Gene, Interaction> subgraph = new UndirectedMaskSubgraph<Gene, Interaction>(
		    interactome, new LimitedMembershipFunctor(lowerbound,
			    upperbound));
	    Writer writer;

	    switch (format) {
	    case dot:
		writer = new FileWriter(file);
		DOTExporter<Gene, Interaction> dotexporter = new DOTExporter<Gene, Interaction>(
			new GeneIdProvider(), new GeneNameProvider(),
			new InteractionNameProvider());

		dotexporter.export(writer, subgraph);
		writer.close();
		return true;

	    case gml:
		writer = new FileWriter(file);
		GmlExporter<Gene, Interaction> gmlexporter = new GmlExporter<Gene, Interaction>();

		gmlexporter.export(writer, subgraph);
		writer.close();
		return true;

	    case graphml:
		writer = new FileWriter(file);
		GraphMLExporter<Gene, Interaction> graphmlexporter = new GraphMLExporter<Gene, Interaction>(
			new GeneIdProvider(), new GeneNameProvider(),
			new InteractionIdProvider(),
			new InteractionNameProvider());

		graphmlexporter.export(writer, subgraph);
		writer.close();
		return true;

	    case adjacency:
	    case laplace:
		writer = new FileWriter(file);
		MatrixExporter<Gene, Interaction> matrixexporter = new MatrixExporter<Gene, Interaction>();
		if (format == FileFormat.adjacency) {
		    matrixexporter.exportAdjacencyMatrix(writer, subgraph);
		} else {
		    matrixexporter.exportLaplacianMatrix(writer, subgraph);
		}
		writer.close();
		return true;
	    default:
		PrintStream print = new PrintStream(file);
		boolean result = write(interactome, format, print, lowerbound,
			upperbound);
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

    public static boolean write(Interactome interactome, FileFormat format,
	    PrintStream print, double lowerbound, double upperbound) {
	Statistics stats;
	try {
	    switch (format) {
	    case interactome:
		printHeader(print, interactome);
		stats = printInteractions(interactome, print, lowerbound,
			upperbound);
		for (Gene gene : interactome.genes())
		    stats.count(gene);
		printFooter(print, stats, interactome);
		return true;
	    case genome:
		printHeader(print, interactome);
		stats = printGenes(interactome, print, lowerbound, upperbound);
		for (Interaction interaction : interactome)
		    stats.count(interaction);
		printFooter(print, stats, interactome);
		return true;
	    case summary:
		printHeader(print, interactome);
		stats = new Statistics(STANDARD_BIN_COUNT, lowerbound,
			upperbound);
		for (Gene gene : interactome.genes())
		    if (gene.getMembership() >= lowerbound
			    && gene.getMembership() <= upperbound)
			stats.count(gene);
		for (Interaction interaction : interactome)
		    if (interaction.getMembership() >= lowerbound
			    && interaction.getMembership() <= upperbound)
			stats.count(interaction);
		printFooter(print, stats, interactome);

	    default:
		return false;
	    }
	} catch (IOException e) {
	    log.error("Could not write to file.", e);
	    return false;
	}
    }

    private File file;

    private FileFormat format;

    private double lowerbound;

    private double upperbound;

    public ToFile(Interactome i, FileFormat format, String filename,
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
	print.print(" @ ");
	print.print(lowerbound);
	print.print(" ");
	print.print(upperbound);
	print.print(" ");
	print.print(format.name());
	print.print(" ");
	print.print("\"");
	print.print(file);
	print.print("\"");
	return print;
    }

    public StringBuilder show(StringBuilder sb) {
	i.show(sb);
	sb.append(" @ ");
	sb.append(lowerbound).append(" ").append(upperbound);
	sb.append(" ");
	sb.append(format.name()).append(" ");
	sb.append("\"").append(file).append("\"");
	return sb;
    }
}
