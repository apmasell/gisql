package ca.wlu.gisql.vm;

/* Moves an object from the operand stack to the variable stack. Used when entering a function. */
public class InstructionVariablePush extends Instruction {

	InstructionVariablePush() {
	}

	@Override
	void execute(Machine machine) {
		machine.variables.push(machine.operands.pop());
	}

	@Override
	public String toString() {
		return "VPUSH";
	}

}
