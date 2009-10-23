package ca.wlu.gisql.vm;

import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * A special type of {@link Program} that is an arbitrary number of
 * {@link InstructionApply}, followed by {@link InstructionReturn}.
 */
class ApplyProgram extends Program {
	private final int count;

	protected ApplyProgram(int count) {
		super();
		this.count = count;
	}

	@Override
	public Instruction get(int index) {
		return index < count ? Instruction.Apply : Instruction.Return;
	}

	public void show(ShowablePrintWriter<Object> print) {
		print.print("{ APPLY: ");
		print.print(count);
		print.print("}");
	}

}
