package ca.wlu.gisql.interactome.logic;

import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ast.AstLogic;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.util.ComputedInteractomeParser;

public class Intersection extends ComputedInteractomeParser {
	public final static ComputedInteractomeParser descriptor = new Intersection();

	public Intersection() {
		super(Parser.PREC_CONJUNCT, '∩', new char[] { '&' },
				"Intersection (Ax t Bx)");

	}

	protected AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeConjunct(left, right);
	}
}
