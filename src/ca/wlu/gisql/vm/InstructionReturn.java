package ca.wlu.gisql.vm;

/** Return to the caller by popping the current call stack frame. */
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
