/**
 * 
 */
package ca.wlu.gisql.db;

import java.util.ArrayList;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.MaybeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.runner.ExpressionRunner;

public final class TaxId2Interactome implements GenericFunction {
	static final Type type = new ArrowType(Type.NumberType, new MaybeType(
			Type.InteractomeType));
	private final ExpressionRunner runner;

	public TaxId2Interactome(ExpressionRunner runner) {
		this.runner = runner;
	}

	@Override
	public String getDescription() {
		return "Find the interactome of the corresponding NCBI taxon identifier.";
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Object run(Object... parameters) {
		long taxid = (Long) parameters[0];
		ArrayList<?> all = (ArrayList<?>) runner.getEnvironment().getVariable(
				"all");
		for (Object interactome : all) {
			if (interactome instanceof DbSpecies
					&& ((DbSpecies) interactome).getId() == taxid) {
				return interactome;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "taxid2species";
	}
}