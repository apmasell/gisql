package ca.wlu.gisql.interactome.patch;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.vm.Machine;

final class PatchFunction extends Function {

	public PatchFunction() {
		super(
				"blanks",
				"Fill in unknown interactions with a value if both genes are present",
				Type.InteractomeType, Type.MembershipType, Type.InteractomeType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		Interactome source = (Interactome) parameters[0];
		double membership = (Double) parameters[1];
		return new Patch(source, membership);
	}

}
