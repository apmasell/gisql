package ca.wlu.gisql.vm;

class InstructionReturn extends Instruction {

	@Override
	void execute(Machine machine) {
		machine.frame = machine.stack.pop();
	}

	@Override
	public String toString() {
		return "RETURN";
	}

}
