package ca.wlu.gisql.interactome.coreicity;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.vm.Machine;

public class JaccardCoreicityFunction extends Function {
	JaccardCoreicityFunction() {
		super(
				"jaccardcore",
				"Compute membership values of interactions as a Jaccard index of the species in their genes",
				Type.InteractomeType, Type.InteractomeType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		Interactome source = (Interactome) parameters[0];
		return new JaccardCoreicity(source);
	}

}
