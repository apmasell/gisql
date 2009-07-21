package ca.wlu.gisql.functions;

import ca.wlu.gisql.environment.parser.ast.AstInteractome;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class Leaf extends Tree {

	private final Interactome source;

	public Leaf(Interactome source) {
		super();
		this.source = source;
	}

	public AstNode getIntersection() {
		return new AstInteractome(source);
	}

	public AstNode getUnion() {
		return new AstInteractome(source);
	}

	public void show(ShowablePrintWriter<Object> print) {
		print.print('"');
		print.print(source);
		print.print('"');
	}

}
