package ca.wlu.gisql.vm;

import ca.wlu.gisql.ast.Function;

/**
 * Calls a class extending {@link Function}, popping an appropriate number of
 * objects from the operand stack.
 */
public class InstructionFunction extends Instruction {

	private final Function function;

	public InstructionFunction(Function function) {
		this.function = function;
	}

	@Override
	void execute(Machine machine) {
		int length = function.getArgumentCount();
		Object[] parameters = new Object[length];
		for (int index = 0; index < length; index++) {
			parameters[index] = machine.operands.pop();
		}
		machine.operands.push(function.run(machine, parameters));
	}

	@Override
	public String toString() {
		return "FUNC " + function.toString();
	}

}
