package ca.wlu.gisql.interactome.logic;

import ca.wlu.gisql.environment.parser.ast.AstLogic;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.util.ComputedInteractomeParser;

public class SymmetricDifference extends ComputedInteractomeParser {

	public final static ComputedInteractomeParser descriptor = new SymmetricDifference();

	public SymmetricDifference() {
		super(4, '∆', new char[] { '^' },
				"Symmetric Difference ((Ax t v(Bx)) s (Bx t v(Ax)))");
	}

	protected AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeDisjunct(AstLogic.makeConjunct(left, AstLogic
				.makeNegation(right)), AstLogic.makeConjunct(right, AstLogic
				.makeNegation(left)));
	}
}
