package ca.wlu.gisql.ast.type;

import ca.wlu.gisql.ast.AstLiteral;

public class Unit {
	public static final Unit nil = new Unit();
	public static final AstLiteral nilAst = new AstLiteral(Type.UnitType, nil);

	private Unit() {
	}

	@Override
	public String toString() {
		return "()";
	}

}
