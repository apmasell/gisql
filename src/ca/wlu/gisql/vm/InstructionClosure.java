package ca.wlu.gisql.vm;

import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * This creates a closure by popping a program and an object from the operand
 * stack, creating a closure, and pushing the result on the operand stack.
 */
class InstructionClosure extends Instruction {

	private static class Closure extends Program {

		private final Program program;
		private final InstructionPush push;

		/**
		 * A program that represents a closure. It will push a stored object
		 * onto the operand stack and delegate to another program.
		 */
		private Closure(Object value, Program program) {
			push = new InstructionPush(value);
			this.program = program;
		}

		@Override
		public Instruction get(int index) {
			if (index == 0) {
				return push;
			} else {
				return program.get(index - 1);
			}
		}

		public void show(ShowablePrintWriter<Object> print) {
			print.print("*: ");
			print.println(push);
			print.print(program);
		}

		@Override
		public String toString() {
			return program.toString() + " " + push.value;
		}

	}

	InstructionClosure() {
	}

	@Override
	void execute(Machine machine) {
		Program program = (Program) machine.operands.pop();
		Object value = machine.operands.pop();
		machine.operands.push(new Closure(value, program));
	}

	@Override
	public String toString() {
		return "CLOSE";
	}

}
