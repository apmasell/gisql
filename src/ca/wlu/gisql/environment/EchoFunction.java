package ca.wlu.gisql.environment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;
import ca.wlu.gisql.environment.parser.ast.AstVoid;
import ca.wlu.gisql.environment.parser.util.Function;

public final class EchoFunction extends Function {
	private class Echo extends AstVoid {
		private final Environment environment;

		private final String string;

		private Echo(Environment environment, AstString string) {
			this.environment = environment;
			this.string = string.getString();
		}

		public void execute() {
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

		}
	}

	public static final Parseable descriptor = new EchoFunction();

	private static final Logger log = Logger.getLogger(EchoFunction.class);

	private EchoFunction() {
		super("echo", new Function.Parameter[] { new Function.QuotedString(
				"text") });
	}

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		return new Echo(environment, (AstString) params.get(0));
	}
}