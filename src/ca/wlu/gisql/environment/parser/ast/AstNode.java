package ca.wlu.gisql.environment.parser.ast;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Prioritizable;
import ca.wlu.gisql.util.Show;

public interface AstNode extends Prioritizable<AstNode>, Show<AstNode> {

	public abstract Interactome asInteractome();

	public abstract AstNode fork(AstNode substitute);

	public abstract boolean isInteractome();
}
