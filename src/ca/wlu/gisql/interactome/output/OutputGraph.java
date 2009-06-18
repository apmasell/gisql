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

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;

class OutputGraph extends AbstractOutput {

	private final SimpleWeightedGraph<Gene, Interaction> graph = new SimpleWeightedGraph<Gene, Interaction>(
			Interaction.class);

	OutputGraph(Interactome source, String name, FileFormat format,
			String filename) {
		super(source, name, format, filename);
	}

	public double calculateMembership(Gene gene) {
		return source.calculateMembership(gene);
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (!GisQL.isMissing(membership)) {
			graph.addVertex(interaction.getGene1());
			graph.addVertex(interaction.getGene2());
			graph.addEdge(interaction.getGene1(), interaction.getGene2(),
					interaction);
		}
		return membership;
	}

	public boolean postpare() {
		if (!super.postpare() || !source.postpare())
			return false;
		try {
			Writer writer = (filename == null ? new PrintWriter(System.out)
					: new FileWriter(filename));
			switch (format) {
			case dot:
				DOTExporter<Gene, Interaction> dotexporter = new DOTExporter<Gene, Interaction>(
						new GeneIdProvider(), new GeneNameProvider(),
						new InteractionNameProvider());
				dotexporter.export(writer, graph);
			case gml:
				GmlExporter<Gene, Interaction> gmlexporter = new GmlExporter<Gene, Interaction>();

				gmlexporter.export(writer, graph);

			case graphml:
				GraphMLExporter<Gene, Interaction> graphmlexporter = new GraphMLExporter<Gene, Interaction>(
						new GeneIdProvider(), new GeneNameProvider(),
						new InteractionIdProvider(),
						new InteractionNameProvider());

				graphmlexporter.export(writer, graph);

			case adjacency:
			case laplace:
				MatrixExporter<Gene, Interaction> matrixexporter = new MatrixExporter<Gene, Interaction>();
				if (format == FileFormat.adjacency) {
					matrixexporter.exportAdjacencyMatrix(writer, graph);
				} else {
					matrixexporter.exportLaplacianMatrix(writer, graph);
				}
			}
			if (filename != null)
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
