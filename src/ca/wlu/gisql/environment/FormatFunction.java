package ca.wlu.gisql.environment;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.util.Function;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Unit;
import ca.wlu.gisql.interactome.output.FileFormat;

public class FormatFunction extends Function {
	class SetFormat extends Unit {
		private final Environment environment;

		private FileFormat format;

		private SetFormat(Environment environment, FileFormat format) {
			this.environment = environment;
			this.format = format;
		}

		public boolean prepare() {
			if (environment instanceof UserEnvironment) {
				((UserEnvironment) environment).setFormat(format);
				return super.prepare();
			} else {
				return false;
			}
		}
	}

	public static final Parseable descriptor = new FormatFunction();

	protected FormatFunction() {
		super("format", new Parameter[] { new Function.Name("layout") });
	}

	public Interactome construct(Environment environment, List<Object> params,
			Stack<String> error) {
		FileFormat format = FileFormat.valueOf((String) params.get(0));
		if (format == null) {
			return null;
		} else {
			return new SetFormat(environment, format);
		}
	}

}
