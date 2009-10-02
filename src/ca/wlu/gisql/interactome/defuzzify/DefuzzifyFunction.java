package ca.wlu.gisql.interactome.defuzzify;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.vm.Machine;

final class DefuzzifyFunction extends Function {

	public DefuzzifyFunction() {
		super(
				"defuzz",
				"Defuzzify memberships of genes and interactions into crisp 0 and 1.",
				Type.InteractomeType, Type.InteractomeType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		Interactome source = (Interactome) parameters[0];
		return new Defuzzify(source);
	}

}
