package ca.wlu.gisql.interactome.output;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Maybe;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.QuotedString;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ProcessableInteractome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public abstract class AbstractOutput extends ProcessableInteractome {
	private static class AstOutput implements AstNode {

		private final String filename;
		private final FileFormat format;
		private final AstNode interactome;
		private final String name;

		private AstOutput(AstNode interactome, String name, FileFormat format,
				String filename) {
			this.interactome = interactome;
			this.name = name;
			this.format = format;
			this.filename = filename;
		}

		public Interactome asInteractome() {
			return wrap(interactome.asInteractome(), name, format, filename,
					true);
		}

		public AstNode fork(AstNode substitute) {
			return new AstOutput(interactome.fork(substitute), name, format,
					filename);
		}

		public int getPrecedence() {
			return descriptor.getPrecedence();
		}

		public boolean isInteractome() {
			return true;
		}

		public void show(ShowablePrintWriter<AstNode> print) {
			print.print(interactome, getPrecedence());
			print.print(" @ ");
			print.print(format.name());
			print.print(" ");
			print.print("\"");
			print.print(filename == null ? "-" : filename);
			print.print("\"");
		}
	}

	public static final Parseable descriptor = new Parseable() {

		private final Token[] tokens = new Token[] { new Maybe(new Name()),
				QuotedString.self };

		public AstNode construct(Environment environment, List<AstNode> params,
				Stack<String> error) {
			AstNode interactome = params.get(0);
			AstString formatname = (AstString) params.get(1);
			FileFormat format = (formatname == null ? FileFormat.interactome
					: FileFormat.valueOf(formatname.getString()));
			String filename = ((AstString) params.get(2)).getString();

			if (!interactome.isInteractome())
				return null;

			if (format == null) {
				format = FileFormat.interactome;
			}

			return new AstOutput(interactome, null, format, filename);
		}

		public int getPrecedence() {
			return Parser.PREC_ASSIGN;
		}

		public boolean isMatchingOperator(char c) {
			return c == '@';
		}

		public boolean isPrefixed() {
			return false;
		}

		public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
			print
					.print("Write to file: A @ [{summary | interactome | genome | dot | gml | graphml | adjacency | laplace}] \"filename\"");
		}

		public Token[] tasks() {
			return tokens;
		}

	};

	protected static final Logger log = Logger.getLogger(OutputGraph.class);

	public static AbstractOutput wrap(Interactome interactome, String name,
			FileFormat format, String filename, boolean force) {
		if (interactome == null)
			return null;
		if (!force && interactome instanceof AbstractOutput)
			return (AbstractOutput) interactome;
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
		set.add(this);
		return source.collectAll(set);
	}

	public int getPrecedence() {
		return descriptor.getPrecedence();
	}

	public Type getType() {
		return source.getType();
	}

	public double membershipOfUnknown() {
		return 0;
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source, this.getPrecedence());
		print.print(" @ ");
		print.print(format.name());
		print.print(" ");
		print.print("\"");
		print.print(filename == null ? "-" : filename);
		print.print("\"");
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}
}
