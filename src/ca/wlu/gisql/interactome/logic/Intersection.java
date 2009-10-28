package ca.wlu.gisql.interactome.logic;

import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.parser.util.ComputedInteractomeDescriptor;

public class Intersection extends ComputedInteractomeDescriptor {
	public static final ComputedInteractomeDescriptor descriptor = new Intersection();

	public Intersection() {
		super(Parser.PREC_CONJUNCT, '∩', new char[] { '&' },
				"Intersection (Ax t Bx)", "and");

	}

	@Override
	protected AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeConjunct(left, right);
	}
}
