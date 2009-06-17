package ca.wlu.gisql.environment;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;
import ca.wlu.gisql.environment.parser.ast.AstVoid;
import ca.wlu.gisql.environment.parser.util.Function;
import ca.wlu.gisql.interactome.output.FileFormat;

public class FormatFunction extends Function {
	private class SetFormat extends AstVoid {
		private final Environment environment;

		private final FileFormat format;

		private SetFormat(Environment environment, FileFormat format) {
			this.environment = environment;
			this.format = format;
		}

		public void execute() {
			if (environment instanceof UserEnvironment) {
				((UserEnvironment) environment).setFormat(format);
			}
		}
	}

	public static final Parseable descriptor = new FormatFunction();

	protected FormatFunction() {
		super("format", new Parameter[] { new Function.Name("layout") });
	}

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		FileFormat format = FileFormat.valueOf(((AstString) params.get(0))
				.getString());
		if (format == null) {
			return null;
		} else {
			return new SetFormat(environment, format);
		}
	}

}
