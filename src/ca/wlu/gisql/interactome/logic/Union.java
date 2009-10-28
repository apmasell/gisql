package ca.wlu.gisql.interactome.logic;

import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.parser.util.ComputedInteractomeDescriptor;

public class Union extends ComputedInteractomeDescriptor {
	public static final ComputedInteractomeDescriptor descriptor = new Union();

	private Union() {
		super(Parser.PREC_DISJUNCT, '∪', new char[] { '|' }, "Union (Ax s Bx)",
				"or");
	}

	@Override
	protected AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeDisjunct(left, right);
	}
}
