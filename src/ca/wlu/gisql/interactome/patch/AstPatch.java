/**
 * 
 */
package ca.wlu.gisql.interactome.patch;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

class AstPatch implements AstNode {
	private final Double membership;
	private final AstNode parameter;

	AstPatch(AstNode parameter, Double membership) {
		this.parameter = parameter;
		this.membership = membership;
	}

	public Interactome asInteractome() {
		return new Patch(parameter.asInteractome(), membership);
	}

	public AstNode fork(AstNode substitute) {
		return new AstPatch(parameter.fork(substitute), membership);
	}

	public int getPrecedence() {
		return Patch.descriptor.getPrecedence();
	}

	public boolean isInteractome() {
		return true;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(parameter, Patch.descriptor.getPrecedence());
		print.print(" $");
		if (membership != null) {
			print.print(" ");
			print.print(membership);
		}
	}
}