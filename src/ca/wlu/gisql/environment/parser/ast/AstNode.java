package ca.wlu.gisql.environment.parser.ast;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Show;

public interface AstNode extends Show {

	public abstract Interactome asInteractome();

	public abstract AstNode fork(AstNode substitute);

	public abstract boolean isInteractome();
}
