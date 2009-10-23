package ca.wlu.gisql.vm;

/**
 * This pops a {@link Program} from the operand stack and executes it. This is
 * meant to be used in conjunction with {@link InstructionClosure}.
 */
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
