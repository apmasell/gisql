package ca.wlu.gisql.interactome.logic;

import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.parser.util.ComputedInteractomeDescriptor;

public class Residuum extends ComputedInteractomeDescriptor {
	public static final ComputedInteractomeDescriptor descriptor = new Residuum();

	public Residuum() {
		super(Parser.PREC_DISJUNCT, '⇒', new char[] { '>' },
				"Residuum (v(Ax) s (Ax t Bx))", "implies");
	}

	@Override
	protected AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeDisjunct(AstLogic.makeNegation(left), AstLogic
				.makeConjunct(left, right));
	}

}
