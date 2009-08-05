/**
 * 
 */
package ca.wlu.gisql.interactome.orphans;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

class AstOrphans implements AstNode {

	private final AstNode parameter;

	public AstOrphans(AstNode parameter) {
		this.parameter = parameter;
	}

	public Interactome asInteractome() {
		return new Orphans(parameter.asInteractome());
	}

	public AstNode fork(AstNode substitute) {
		return new AstOrphans(parameter.fork(substitute));
	}

	public int getPrecedence() {
		return Orphans.descriptor.getPrecedence();
	}

	public boolean isInteractome() {
		return true;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(parameter, getPrecedence());
		print.print(" : orphans");
	}

}