package ca.wlu.gisql.vm;

/**
 * An instruction that manipulates the current state of the {@link Machine}.
 * Instructions must be defined in this package to manipulate the internals of
 * the machine. Singleton instructions are defined here.
 */
public abstract class Instruction {
	public static final Instruction Apply = new InstructionApply();
	public static final Instruction Close = new InstructionClosure();
	public static final Instruction PopVariable = new InstructionVariablePop();
	public static final Instruction PushVariable = new InstructionVariablePush();
	public static final Instruction Return = new InstructionReturn();

	abstract void execute(Machine machine);
}
