package ca.wlu.gisql.interactome.output;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Decimal;
import ca.wlu.gisql.environment.parser.Maybe;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.NextTask;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.QuotedString;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.InteractomeUtil;

public abstract class AbstractOutput extends CachedInteractome {
	public static final Parseable descriptor = new Parseable() {

		public Interactome construct(Environment environment,
				List<Object> params, Stack<String> error) {
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

			/*
			 * For the alpha cut, {Ax | x ∈ [lowerbound, upperbound]}. Normally,
			 * [α, 1]. That means lower should be filled preferentially, which
			 * it is.
			 */
			if (upperbound == null) {
				upperbound = 1.0;
			}
			if (lowerbound == null) {
				lowerbound = 0.0;
			}
			return wrap(interactome, null, lowerbound, upperbound, format,
					filename, true);
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
					.print("Write to file: A @ [lowerbound [upperbound]] [{summary | interactome | genome | dot | gml | graphml | adjacency | laplace}] \"filename\"");
			return print;
		}

		public StringBuilder show(StringBuilder sb) {
			sb
					.append("Write to file: A @ [lowerbound [upperbound]] [{summary | interactome | genome | dot | gml | graphml | adjacency | laplace}] \"filename\"");
			return sb;
		}

		public NextTask[] tasks(Parser parser) {
			return new NextTask[] { new Maybe(parser, new Decimal(parser)),
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
		return descriptor.getNestingLevel();
	}

	public PrintStream show(PrintStream print) {
		InteractomeUtil.precedenceShow(print, source, this.getPrecedence());
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
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		InteractomeUtil.precedenceShow(sb, source, this.getPrecedence());
		sb.append(" @ ");
		sb.append(lowerbound).append(" ").append(upperbound);
		sb.append(" ");
		sb.append(format.name()).append(" ");
		sb.append("\"").append(filename == null ? "-" : filename).append("\"");
		return sb;
	}
}
