package ca.wlu.gisql.interactome.logic;

import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.util.ComputedInteractomeDescriptor;
import ca.wlu.gisql.util.Precedence;

public class SymmetricDifference extends ComputedInteractomeDescriptor {

	public static final ComputedInteractomeDescriptor descriptor = new SymmetricDifference();

	public SymmetricDifference() {
		super(Precedence.Junction, '∆', new char[] { '^' },
				"Symmetric Difference ((Ax t v(Bx)) s (Bx t v(Ax)))", "symdiff");
	}

	@Override
	protected AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeDisjunct(AstLogic.makeConjunct(left, AstLogic
				.makeNegation(right)), AstLogic.makeConjunct(right, AstLogic
				.makeNegation(left)));
	}
}
