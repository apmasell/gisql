package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ast.AstLogic;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.util.ComputedInteractomeParser;
import ca.wlu.gisql.fuzzy.TriangularNorm;

public class Difference extends ComputedInteractomeParser {
	public final static ComputedInteractomeParser descriptor = new Difference();

	Difference() {
		super(Parser.PREC_DIFF, '∖', new char[] { '-', '\\' },
				"Difference (Ax t v(Bx))");
	}

	protected AstNode construct(AstNode left, AstNode right, TriangularNorm norm) {
		return AstLogic.makeConjunct(left, AstLogic.makeNegation(right, norm),
				norm);
	}
}
