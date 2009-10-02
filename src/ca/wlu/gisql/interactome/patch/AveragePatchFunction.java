package ca.wlu.gisql.interactome.patch;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.vm.Machine;

final class AveragePatchFunction extends Function {

	public AveragePatchFunction() {
		super(
				"avgblanks",
				"Fill in unknown interactions with the average value from the other species if both genes are present",
				Type.InteractomeType, Type.InteractomeType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		Interactome source = (Interactome) parameters[0];
		return new Patch(source, null);
	}

}
