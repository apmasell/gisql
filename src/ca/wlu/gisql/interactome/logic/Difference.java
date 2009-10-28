package ca.wlu.gisql.interactome.logic;

import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.parser.util.ComputedInteractomeDescriptor;

public class Difference extends ComputedInteractomeDescriptor {
	public static final ComputedInteractomeDescriptor descriptor = new Difference();

	Difference() {
		super(Parser.PREC_DIFF, '∖', new char[] { '-', '\\' },
				"Difference (Ax t v(Bx))", "diff");
	}

	@Override
	protected AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeConjunct(left, AstLogic.makeNegation(right));
	}
}
