package ca.wlu.gisql.vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InstructionPack extends Instruction {

	private final int size;

	public InstructionPack(int size) {
		this.size = size;
	}

	@Override
	void execute(Machine machine) {
		List<Object> result = new ArrayList<Object>();
		for (int count = 0; count < size; count++) {
			result.add(machine.operands.pop());
		}
		Collections.reverse(result);
		machine.operands.push(result);
	}

	@Override
	public String toString() {
		return "PACK " + size;
	}

}
