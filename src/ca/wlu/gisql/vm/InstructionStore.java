package ca.wlu.gisql.vm;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.type.Type;

/**
 * Copy the object on the top of the operand stack into the environment with the
 * specified name.
 */
public class InstructionStore extends Instruction {

	private final String name;
	private final Type type;

	public InstructionStore(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	void execute(Machine machine) {
		Object result = machine.operands.peek();
		machine.environment.setVariable(name, new AstLiteral(type, result));
	}

	@Override
	public String toString() {
		return "STORE " + name;
	}

}
