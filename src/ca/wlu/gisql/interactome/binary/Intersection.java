package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ast.AstLogic;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.util.ComputedInteractomeParser;
import ca.wlu.gisql.fuzzy.TriangularNorm;

public class Intersection extends ComputedInteractomeParser {
	public final static ComputedInteractomeParser descriptor = new Intersection();

	public Intersection() {
		super(Parser.PREC_CONJUNCT, '∩', new char[] { '&' },
				"Intersection (Ax t Bx)");

	}

	protected AstNode construct(AstNode left, AstNode right, TriangularNorm norm) {
		return AstLogic.makeConjunct(left, right, norm);
	}
}
