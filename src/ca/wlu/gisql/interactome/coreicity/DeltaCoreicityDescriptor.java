package ca.wlu.gisql.interactome.coreicity;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.vm.Machine;
import ca.wlu.gisql.vm.Program;

public class DeltaCoreicityDescriptor extends Function {
	DeltaCoreicityDescriptor() {
		super(
				"deltacore",
				"Filter interactions based on the coreicity difference of their genes",
				Type.InteractomeType, new ArrowType(Type.NumberType,
						Type.BooleanType), Type.InteractomeType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		return new DeltaCoreicity((Interactome) parameters[0], machine
				.duplicate(), (Program) parameters[1]);
	}
}
