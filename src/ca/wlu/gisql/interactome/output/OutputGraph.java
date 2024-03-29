package ca.wlu.gisql.interactome.output;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.xml.transform.TransformerConfigurationException;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.GmlExporter;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.ext.MatrixExporter;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.xml.sax.SAXException;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.GraphedInteractome;
import ca.wlu.gisql.interactome.Interactome;

class OutputGraph extends AbstractOutput {

	OutputGraph(Interactome source, String name, FileFormat format,
			String filename) {
		super(new GraphedInteractome(source), name, format, filename);
	}

	public double calculateMembership(Gene gene) {
		return source.calculateMembership(gene);
	}

	public double calculateMembership(Interaction interaction) {
		return source.calculateMembership(interaction);
	}

	@Override
	public boolean postpare() {
		if (!super.postpare() || !source.postpare()) {
			return false;
		}
		try {
			Writer writer = filename == null ? new PrintWriter(System.out)
					: new FileWriter(filename);
			SimpleWeightedGraph<Gene, Interaction> graph = ((GraphedInteractome) source)
					.getGraph();
			switch (format) {
			case dot:
				DOTExporter<Gene, Interaction> dotexporter = new DOTExporter<Gene, Interaction>(
						new GeneIdProvider(), new GeneNameProvider(),
						new InteractionNameProvider());
				dotexporter.export(writer, graph);
				break;
			case gml:
				GmlExporter<Gene, Interaction> gmlexporter = new GmlExporter<Gene, Interaction>();

				gmlexporter.export(writer, graph);
				break;
			case graphml:
				GraphMLExporter<Gene, Interaction> graphmlexporter = new GraphMLExporter<Gene, Interaction>(
						new GeneIdProvider(), new GeneNameProvider(),
						new InteractionIdProvider(),
						new InteractionNameProvider());

				graphmlexporter.export(writer, graph);
				break;
			case adjacency:
			case laplace:
				MatrixExporter<Gene, Interaction> matrixexporter = new MatrixExporter<Gene, Interaction>();
				if (format == FileFormat.adjacency) {
					matrixexporter.exportAdjacencyMatrix(writer, graph);
				} else {
					matrixexporter.exportLaplacianMatrix(writer, graph);
				}
			}
			writer.close();
			return true;
		} catch (IOException e) {
			log.error("Could not write to file.", e);
			return false;
		} catch (TransformerConfigurationException e) {
			log.error("Could not write to file.", e);
			return false;
		} catch (SAXException e) {
			log.error("SAX error writing to file.", e);
			return false;
		}
	}

	public boolean prepare() {
		return source.prepare();
	}
}
