package ca.wlu.gisql.vm;

final class InstructionPopVariable extends Instruction {
	@Override
	void execute(Machine machine) {
		machine.variables.pop();
	}

	@Override
	public String toString() {
		return "VPOP";
	}
}