package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.environment.parser.ast.AstLogic;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.util.ComputedInteractomeParser;
import ca.wlu.gisql.fuzzy.TriangularNorm;

public class Union extends ComputedInteractomeParser {
	public final static ComputedInteractomeParser descriptor = new Union();

	private Union() {
		super(2, '∪', new char[] { '|' }, "Union (Ax s Bx)");
	}

	protected AstLogic construct(AstNode left, AstNode right,
			TriangularNorm norm) {
		return AstLogic.makeDisjunct(left, right, norm);
	}
}
