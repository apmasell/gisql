package ca.wlu.gisql.vm;

public class InstructionApply extends Instruction {

	@Override
	void execute(Machine machine) {
		machine.stack.push(machine.frame);
		machine.frame = new Frame((Program) machine.operands.pop());
	}

	@Override
	public String toString() {
		return "APPLY";
	}

}
