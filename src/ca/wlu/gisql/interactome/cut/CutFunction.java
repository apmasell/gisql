package ca.wlu.gisql.interactome.cut;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.vm.Machine;

class CutFunction extends Function {

	public CutFunction() {
		super(
				"cut",
				"Filter interactions and genes with a score lower than threshold",
				Type.InteractomeType, Type.MembershipType, Type.InteractomeType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		Interactome source = (Interactome) parameters[0];
		double cutoff = (Double) parameters[1];
		return new Cut(source, cutoff);
	}

}
