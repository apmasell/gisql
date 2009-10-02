package ca.wlu.gisql.vm;

import ca.wlu.gisql.util.ShowablePrintWriter;

class InstructionClosure extends Instruction {

	private static class Closure extends Program {

		private final Program program;
		private final InstructionPush push;

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
