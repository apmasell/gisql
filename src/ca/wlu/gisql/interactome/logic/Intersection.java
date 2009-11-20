package ca.wlu.gisql.interactome.logic;

import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.util.ComputedInteractomeDescriptor;
import ca.wlu.gisql.util.Precedence;

public class Intersection extends ComputedInteractomeDescriptor {
	public static final ComputedInteractomeDescriptor descriptor = new Intersection();

	public Intersection() {
		super(Precedence.Junction, '∩', new char[] { '&' },
				"Intersection (Ax t Bx)", "and");

	}

	@Override
	public AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeConjunct(left, right);
	}
}
