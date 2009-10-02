package ca.wlu.gisql.interactome.orphans;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.vm.Machine;

final class OrphansFunction extends Function {

	public OrphansFunction() {
		super("orphans", "Filter genes that are disconnected",
				Type.InteractomeType, Type.InteractomeType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		Interactome source = (Interactome) parameters[0];
		return new Orphans(source);
	}

}
