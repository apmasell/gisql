package ca.wlu.gisql.ast.type;

/** The type of nothing. Loosely equivalent to the void type in Java. */
public class Unit {
	public static final Unit nil = new Unit();

	private Unit() {
	}

	@Override
	public String toString() {
		return "()";
	}

}
