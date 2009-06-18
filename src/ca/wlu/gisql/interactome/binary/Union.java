package ca.wlu.gisql.interactome.binary;

import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ast.AstLogic;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.util.ComputedInteractomeParser;

public class Union extends ComputedInteractomeParser {
	public final static ComputedInteractomeParser descriptor = new Union();

	private Union() {
		super(Parser.PREC_DISJUNCT, '∪', new char[] { '|' }, "Union (Ax s Bx)");
	}

	protected AstNode construct(AstNode left, AstNode right) {
		return AstLogic.makeDisjunct(left, right);
	}
}
