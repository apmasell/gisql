package ca.wlu.gisql.interactome.output;

import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Decimal;
import ca.wlu.gisql.environment.parser.Maybe;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.QuotedString;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstDouble;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public abstract class AbstractOutput extends CachedInteractome {
	private static class AstOutput implements AstNode {

		private final String filename;
		private final FileFormat format;
		private final AstNode interactome;
		private final double lbound;
		private final String name;
		private final double ubound;

		public AstOutput(AstNode interactome, String name, double lbound,
				double ubound, FileFormat format, String filename) {
			this.interactome = interactome;
			this.name = name;
			this.lbound = lbound;
			this.ubound = ubound;
			this.format = format;
			this.filename = filename;
		}

		public Interactome asInteractome() {
			return wrap(interactome.asInteractome(), name, lbound, ubound,
					format, filename, true);
		}

		public AstNode fork(AstNode substitute) {
			return new AstOutput(interactome.fork(substitute), name, lbound,
					ubound, format, filename);
		}

		public int getPrecedence() {
			return descriptor.getPrecedence();
		}

		public boolean isInteractome() {
			return true;
		}

		public void show(ShowablePrintWriter print) {
			print.print(interactome, getPrecedence());
			print.print(" @ ");
			print.print(lbound);
			print.print(" ");
			print.print(ubound);
			print.print(" ");
			print.print(format.name());
			print.print(" ");
			print.print("\"");
			print.print(filename == null ? "-" : filename);
			print.print("\"");
		}
	}

	public static final Parseable descriptor = new Parseable() {

		public AstNode construct(Environment environment, List<AstNode> params,
				Stack<String> error) {
			AstNode interactome = params.get(0);
			AstDouble lowerbound = (AstDouble) params.get(1);
			AstDouble upperbound = (AstDouble) params.get(2);
			AstString formatname = (AstString) params.get(3);
			FileFormat format = (formatname == null ? FileFormat.interactome
					: FileFormat.valueOf(formatname.getString()));
			String filename = ((AstString) params.get(4)).getString();

			if (!interactome.isInteractome())
				return null;

			if (format == null) {
				format = FileFormat.interactome;
			}

			/*
			 * For the alpha cut, {Ax | x ∈ [lowerbound, upperbound]}. Normally,
			 * [α, 1]. That means lower should be filled preferentially, which
			 * it is.
			 */
			double ubound = (upperbound == null ? 1.0 : upperbound.getDouble());
			double lbound = (lowerbound == null ? 0.0 : lowerbound.getDouble());
			return new AstOutput(interactome, null, lbound, ubound, format,
					filename);
		}

		public int getPrecedence() {
			return 0;
		}

		public boolean isMatchingOperator(char c) {
			return c == '@';
		}

		public boolean isPrefixed() {
			return false;
		}

		public void show(ShowablePrintWriter print) {
			print
					.print("Write to file: A @ [lowerbound [upperbound]] [{summary | interactome | genome | dot | gml | graphml | adjacency | laplace}] \"filename\"");
		}

		public Token[] tasks(Parser parser) {
			return new Token[] { new Maybe(parser, new Decimal(parser)),
					new Maybe(parser, new Decimal(parser)),
					new Maybe(parser, new Name(parser)),
					new QuotedString(parser) };
		}

	};

	protected static final Logger log = Logger.getLogger(OutputGraph.class);

	public static AbstractOutput wrap(Interactome interactome, String name,
			double lowerbound, double upperbound, FileFormat format,
			String filename, boolean force) {
		if (interactome == null)
			return null;
		if (!force && interactome instanceof AbstractOutput)
			return (AbstractOutput) interactome;
		switch (format) {
		case genome:
		case interactome:
		case summary:
			return new OutputText(interactome, name, lowerbound, upperbound,
					format, filename);
		default:
			return new OutputGraph(interactome, name, lowerbound, upperbound,
					format, filename);

		}
	}

	protected final String filename;

	protected final FileFormat format;

	protected AbstractOutput(Interactome source, String name,
			double lowerbound, double upperbound, FileFormat format,
			String filename) {
		super(source, name, lowerbound, upperbound);
		this.format = format;
		this.filename = filename;
	}

	public int getPrecedence() {
		return descriptor.getPrecedence();
	}

	public void show(ShowablePrintWriter print) {
		print.print(source, this.getPrecedence());
		print.print(" @ ");
		print.print(lowerbound);
		print.print(" ");
		print.print(upperbound);
		print.print(" ");
		print.print(format.name());
		print.print(" ");
		print.print("\"");
		print.print(filename == null ? "-" : filename);
		print.print("\"");
	}
}
