package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.environment.parser.ast.AstLogic;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.util.ComputedInteractomeParser;
import ca.wlu.gisql.fuzzy.TriangularNorm;

public class Residuum extends ComputedInteractomeParser {
	public final static ComputedInteractomeParser descriptor = new Residuum();

	public Residuum() {
		super(2, '⇒', new char[] { '>' }, "Residuum (v(Ax) s (Ax t Bx))");
	}

	protected AstLogic construct(AstNode left, AstNode right,
			TriangularNorm norm) {
		return AstLogic.makeDisjunct(AstLogic.makeNegation(left, norm),
				AstLogic.makeConjunct(left, right, norm), norm);
	}

}
