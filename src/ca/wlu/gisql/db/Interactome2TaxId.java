/**
 * 
 */
package ca.wlu.gisql.db;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.MaybeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionRunner;

public final class Interactome2TaxId implements GenericFunction {
	static final Type type = new ArrowType(Type.InteractomeType, new MaybeType(
			Type.NumberType));

	public Interactome2TaxId(ExpressionRunner runner) {
	}

	@Override
	public String getDescription() {
		return "Find the NCBI taxon identifier of an interactome, if one exists.";
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Object run(Object... parameters) {
		Interactome interactome = (Interactome) parameters[0];
		return interactome instanceof TaxonomicInteractome ? ((TaxonomicInteractome) interactome)
				.getId()
				: null;
	}

	@Override
	public String toString() {
		return "interactome2taxid";
	}
}