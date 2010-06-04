package ca.wlu.gisql.interactome.output;

import java.util.Set;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ProcessableInteractome;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public abstract class AbstractOutput extends ProcessableInteractome {
	public static final Parseable<AstNode, Precedence> descriptor = new OutputDescriptor();

	protected static final Logger log = Logger.getLogger(OutputGraph.class);

	public static AbstractOutput wrap(Interactome interactome, String name,
			FileFormat format, String filename, boolean force) {
		if (interactome == null) {
			return null;
		}
		if (!force && interactome instanceof AbstractOutput) {
			return (AbstractOutput) interactome;
		}

		if (filename != null && filename.equals("-")) {
			filename = null;
		}

		switch (format) {
		case genome:
		case interactome:
		case summary:
			return new OutputText(interactome, name, format, filename);
		default:
			return new OutputGraph(interactome, name, format, filename);

		}
	}

	protected final String filename;

	protected final FileFormat format;

	protected final Interactome source;

	protected AbstractOutput(Interactome source, String name,
			FileFormat format, String filename) {
		this.source = source;
		this.format = format;
		this.filename = filename;
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		return source.collectAll(set);
	}

	public Construction getConstruction() {
		return source.getConstruction();
	}

	public Precedence getPrecedence() {
		return descriptor.getPrecedence();
	}

	public double membershipOfUnknown() {
		return 0;
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source, getPrecedence());
		print.print(" @ ");
		print.print(format.name());
		print.print(" ");
		print.print("\"");
		print.print(filename == null ? "-" : filename);
		print.print("\"");
	}
}
