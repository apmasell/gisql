package ca.wlu.gisql.environment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.util.Function;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Unit;

public final class EchoFunction extends Function {
	class Echo extends Unit {
		private final Environment environment;

		private final String string;

		private Echo(Environment environment, String string) {
			this.environment = environment;
			this.string = string;
		}

		public boolean prepare() {
			if (environment instanceof UserEnvironment) {
				String filename = ((UserEnvironment) environment).getOutput();
				PrintStream print;
				try {
					print = (filename == null ? System.out : new PrintStream(
							new FileOutputStream(filename, true)));
					print.println(string);
				} catch (IOException e) {
					log.error("Failed to echo.", e);
				}
			}

			return super.prepare();
		}
	}

	public static final Parseable descriptor = new EchoFunction();

	static final Logger log = Logger.getLogger(EchoFunction.class);

	private EchoFunction() {
		super("echo", new Function.Parameter[] { new Function.QuotedString(
				"text") });
	}

	public Interactome construct(Environment environment, List<Object> params,
			Stack<String> error) {
		return new Echo(environment, (String) params.get(0));
	}
}