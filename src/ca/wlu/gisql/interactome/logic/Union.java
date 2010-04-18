package ca.wlu.gisql.interactome.logic;

import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.util.ComputedInteractomeDescriptor;
import ca.wlu.gisql.util.Precedence;

public class Union extends ComputedInteractomeDescriptor {
	public static final ComputedInteractomeDescriptor descriptor = new Union();

	private Union() {
		super(Precedence.Disjunction, new char[] { '∪', '|' },
				"Union (Ax s Bx)", "or");
	}

	@Override
	public AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeDisjunct(left, right);
	}
}
