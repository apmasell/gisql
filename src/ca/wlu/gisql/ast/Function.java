package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.vm.InstructionFunction;
import ca.wlu.gisql.vm.Machine;

public abstract class Function extends AstNative {
	public Function(String name, String description, Type... types) {
		super(name, description, types);
	}

	@Override
	public final boolean render(ProgramRoutine program, int depth, int debrujin) {
		if (depth >= types.length - 1) {
			return program.instructions.add(new InstructionFunction(this));
		} else {
			return wrap(program, depth, debrujin);
		}
	}

	public abstract Object run(Machine machine, Object... parameters);

}
