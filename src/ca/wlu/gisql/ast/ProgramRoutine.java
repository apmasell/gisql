package ca.wlu.gisql.ast;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.vm.Instruction;
import ca.wlu.gisql.vm.Program;

/** A {@link Program} genereated by {@link AstNode}s. */
public class ProgramRoutine extends Program {

	private final String command;

	final List<Instruction> instructions = new ArrayList<Instruction>();

	public ProgramRoutine(String command) {
		super();
		this.command = command;
	}

	/** Finalizes a program by adding a return statement. */
	public ProgramRoutine finish() {
		instructions.add(Instruction.Return);
		return this;
	}

	@Override
	public Instruction get(int index) {
		if (index >= instructions.size()) {
			return Instruction.Return;
		} else {
			return instructions.get(index);
		}
	}

	public void show(ShowablePrintWriter<Object> print) {
		for (int index = 0; index < instructions.size(); index++) {
			print.print('\t');
			print.print(index);
			print.print(": ");
			print.println(instructions.get(index));
		}
	}

	@Override
	public final String toString() {
		return command;
	}
}
