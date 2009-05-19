package ca.wlu.gisql.environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Unit;
import ca.wlu.gisql.interactome.Interactome.Type;
import ca.wlu.gisql.interactome.output.AbstractOutput;
import ca.wlu.gisql.util.Parseable;

public class EnvironmentUtils {
	public static final Parseable clearDescriptor = new Function("clear", null) {
		class Clear extends Unit {
			private Environment environment;

			protected Clear(Environment environment) {
				this.environment = environment;
			}

			public boolean prepare() {
				clear(environment);
				return super.prepare();
			}
		}

		public Interactome construct(Environment environment,
				List<Object> params, Stack<String> error) {
			return new Clear(environment);
		}
	};

	private static final Logger log = Logger.getLogger(EnvironmentUtils.class);

	public static final Parseable runDescriptor = new Function("run",
			new Function.Parameter[] { new Function.QuotedString("filename") }) {
		class Script extends Unit {
			private UserEnvironment environment;

			private File file;

			protected Script(Environment environment, File file) {
				this.environment = (UserEnvironment) environment;
				this.file = file;
			}

			public boolean prepare() {
				if (!super.prepare())
					return false;
				return runFile(environment, file);

			}
		}

		public Interactome construct(Environment environment,
				List<Object> params, Stack<String> error) {
			File file = new File((String) params.get(0));
			if (file.canRead()) {
				return new Script(environment, file);
			} else {
				return null;
			}
		}
	};

	public static final Parseable variableDescriptor = new Parseable() {
		public Interactome construct(Environment environment,
				List<Object> params, Stack<String> error) {
			String name = (String) params.get(0);
			Interactome result = environment.getVariable(name);
			if (result == null)
				error.push("Undefined variable: " + name);
			return result;
		}

		public int getNestingLevel() {
			return 6;
		}

		public boolean isMatchingOperator(char c) {
			return c == '$';
		}

		public boolean isPrefixed() {
			return true;
		}

		public PrintStream show(PrintStream print) {
			print.print("Read a variable: $varname");
			return print;
		}

		public StringBuilder show(StringBuilder sb) {
			sb.append("Read a variable: $varname");
			return sb;
		}

		public Parser.NextTask[] tasks(Parser parser) {
			return new Parser.NextTask[] { parser.new Name() };
		}

	};

	public static void clear(Environment environment) {
		for (String name : environment.names(Type.Mutable)) {
			environment.setVariable(name, null);
		}
	}

	public static boolean runExpression(UserEnvironment environment,
			String expression) {
		Parser parser = new Parser(environment, expression);
		Interactome interactome = AbstractOutput.wrap(parser.get(), 0.0, 1.0,
				environment.getFormat(), environment.getOutput(), false);
		environment.append(interactome);
		if (interactome == null) {
			log.error(parser.getErrors());
			return false;
		}
		return true;
	}

	public static boolean runFile(UserEnvironment environment, File file) {
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line;
			int linenumber = 0;
			while ((line = input.readLine()) != null) {
				linenumber++;
				if (!runExpression(environment, line)) {
					log.error("Script failed on line :" + linenumber);
					return false;
				}
			}
			input.close();
			return true;
		} catch (IOException e) {
			log.error("Script error.", e);
		}
		return true;
	}

}
