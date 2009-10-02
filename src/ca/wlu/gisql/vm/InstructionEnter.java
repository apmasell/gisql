package ca.wlu.gisql.vm;

public class InstructionEnter extends Instruction {

	InstructionEnter() {
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
