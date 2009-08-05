/**
 * 
 */
package ca.wlu.gisql.interactome.level;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

class AstLevel implements AstNode {
	private final AstNode parameter;

	public AstLevel(AstNode parameter) {
		this.parameter = parameter;
	}

	public Interactome asInteractome() {
		return new Level(parameter.asInteractome());
	}

	public AstNode fork(AstNode substitute) {
		return new AstLevel(parameter.fork(substitute));
	}

	public int getPrecedence() {
		return Level.descriptor.getPrecedence();
	}

	public boolean isInteractome() {
		return true;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(parameter, getPrecedence());
		print.print(" =");
	}

}