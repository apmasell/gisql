package ca.wlu.gisql.vm;

public abstract class Instruction {
	public static final Instruction Apply = new InstructionApply();
	public static final Instruction Close = new InstructionClosure();
	public static final Instruction PopVariable = new InstructionPopVariable();
	public static final Instruction PushVariable = new InstructionEnter();
	public static final Instruction Return = new InstructionReturn();

	abstract void execute(Machine machine);
}
