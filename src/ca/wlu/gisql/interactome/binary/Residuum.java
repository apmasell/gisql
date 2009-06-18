package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ast.AstLogic;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.util.ComputedInteractomeParser;

public class Residuum extends ComputedInteractomeParser {
	public final static ComputedInteractomeParser descriptor = new Residuum();

	public Residuum() {
		super(Parser.PREC_DISJUNCT, '⇒', new char[] { '>' },
				"Residuum (v(Ax) s (Ax t Bx))");
	}

	protected AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeDisjunct(AstLogic.makeNegation(left), AstLogic
				.makeConjunct(left, right));
	}

}
