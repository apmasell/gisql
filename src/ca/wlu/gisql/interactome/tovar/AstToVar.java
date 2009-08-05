/**
 * 
 */
package ca.wlu.gisql.interactome.tovar;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

class AstToVar implements AstNode {
	private final Environment environment;
	private final AstNode interactome;
	private final String name;

	AstToVar(Environment environment, AstNode node, String name) {
		this.environment = environment;
		interactome = node;
		this.name = name;
	}

	public Interactome asInteractome() {
		return new ToVar(environment, interactome.asInteractome(), name);
	}

	public AstNode fork(AstNode substitute) {
		return new AstToVar(environment, interactome.fork(substitute), name);
	}

	public int getPrecedence() {
		return ToVar.descriptor.getPrecedence();
	}

	public boolean isInteractome() {
		return true;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(interactome);
		print.print(" @ ");
		print.print(name);
	}
}