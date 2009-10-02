package ca.wlu.gisql.vm;

public class InstructionPush extends Instruction {

	final Object value;

	public InstructionPush(Object value) {
		this.value = value;
	}

	@Override
	void execute(Machine machine) {
		machine.operands.push(value);
	}

	@Override
	public String toString() {
		return "PUSH {" + value.toString() + "}";
	}

}
