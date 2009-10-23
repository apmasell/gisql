package ca.wlu.gisql.vm;

/** Pops the top item from the variable stack and discards it. */
final class InstructionVariablePop extends Instruction {
	@Override
	void execute(Machine machine) {
		machine.variables.pop();
	}

	@Override
	public String toString() {
		return "VPOP";
	}
}