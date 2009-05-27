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
import org.xml.sax.SAXException;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;

class OutputGraph extends AbstractOutput {

	OutputGraph(Interactome source, String name, double lowerbound,
			double upperbound, FileFormat format, String filename) {
		super(source, name, lowerbound, upperbound, format, filename);
	}

	public Interactome fork(Interactome substitute) {
		return new OutputGraph(source.fork(substitute), getName(), lowerbound,
				upperbound, format, filename);
	}

	public boolean postpare() {
		if (!super.postpare())
			return false;
		try {
			Writer writer = (filename == null ? new PrintWriter(System.out)
					: new FileWriter(filename));
			switch (format) {
			case dot:
				DOTExporter<Gene, Interaction> dotexporter = new DOTExporter<Gene, Interaction>(
						new GeneIdProvider(), new GeneNameProvider(),
						new InteractionNameProvider());
				dotexporter.export(writer, getGraph());
			case gml:
				GmlExporter<Gene, Interaction> gmlexporter = new GmlExporter<Gene, Interaction>();

				gmlexporter.export(writer, getGraph());

			case graphml:
				GraphMLExporter<Gene, Interaction> graphmlexporter = new GraphMLExporter<Gene, Interaction>(
						new GeneIdProvider(), new GeneNameProvider(),
						new InteractionIdProvider(),
						new InteractionNameProvider());

				graphmlexporter.export(writer, getGraph());

			case adjacency:
			case laplace:
				MatrixExporter<Gene, Interaction> matrixexporter = new MatrixExporter<Gene, Interaction>();
				if (format == FileFormat.adjacency) {
					matrixexporter.exportAdjacencyMatrix(writer, this
							.getGraph());
				} else {
					matrixexporter.exportLaplacianMatrix(writer, this
							.getGraph());
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
}
