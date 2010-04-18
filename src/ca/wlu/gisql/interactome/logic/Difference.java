package ca.wlu.gisql.interactome.logic;

import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.util.ComputedInteractomeDescriptor;
import ca.wlu.gisql.util.Precedence;

public class Difference extends ComputedInteractomeDescriptor {
	public static final ComputedInteractomeDescriptor descriptor = new Difference();

	Difference() {
		super(Precedence.Difference, new char[] { '∖', '-', '\\' },
				"Difference (Ax t v(Bx))", "diff");
	}

	@Override
	public AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeConjunct(left, AstLogic.makeNegation(right));
	}
}
