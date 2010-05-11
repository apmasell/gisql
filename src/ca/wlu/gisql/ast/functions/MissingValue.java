package ca.wlu.gisql.ast.functions;

import ca.wlu.gisql.ast.AstNative;
import ca.wlu.gisql.ast.type.MaybeType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Rendering;

/** The null value. */
public class MissingValue extends AstNative {

	public MissingValue() {
		super("missing", "Lack of a value", new MaybeType(new TypeVariable()));
	}

	@Override
	protected <C> boolean renderSelf(Rendering<C> program, int depth) {
		return program.hO_AsObject(null);
	}

}
