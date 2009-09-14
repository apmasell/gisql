package ca.wlu.gisql.environment;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstVoid;
import ca.wlu.gisql.environment.parser.util.Function;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.ProcessableInteractome;

public final class SilentFunction extends Function {
	public static Parseable descriptor = new SilentFunction();

	public class AstSilent extends AstVoid {

		private final AstNode interactome;

		public AstSilent(AstNode interactome) {
			this.interactome = interactome;
		}

		public void execute() {
			ProcessableInteractome processableInteractome = CachedInteractome
					.wrap(interactome.asInteractome(), null);
			processableInteractome.process();
		}

	}

	protected SilentFunction() {
		super("silent", new Function.Parameter[] { new Function.Expression() });
	}

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		AstNode interactome = params.get(0);
		if (interactome.isInteractome())
			return new AstSilent(interactome);
		else
			return null;
	}

}
