package ca.wlu.gisql.vm;

public class InstructionVariable extends Instruction {

	private final int debrujin;

	public InstructionVariable(int debrujin) {
		this.debrujin = debrujin;
	}

	@Override
	void execute(Machine machine) {
		int index = machine.variables.size() - debrujin - 1;
		machine.operands.push(machine.variables.get(index));
	}

	@Override
	public String toString() {
		return "LOAD " + debrujin;
	}

}