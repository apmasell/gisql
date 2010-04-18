package ca.wlu.gisql.interactome.logic;

import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.util.ComputedInteractomeDescriptor;
import ca.wlu.gisql.util.Precedence;

public class Residuum extends ComputedInteractomeDescriptor {
	public static final ComputedInteractomeDescriptor descriptor = new Residuum();

	public Residuum() {
		super(Precedence.Disjunction, new char[] { '⇒', '>' },
				"Residuum (v(Ax) s (Ax t Bx))", "implies");
	}

	@Override
	public AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeDisjunct(AstLogic.makeNegation(left), AstLogic
				.makeConjunct(left, right));
	}

}
