package ca.wlu.gisql.vm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class InstructionConstruct extends Instruction {

	private final Constructor<?> constructor;
	private final int length;

	public InstructionConstruct(Constructor<?> constructor) {
		this.constructor = constructor;
		length = constructor.getParameterTypes().length;
	}

	@Override
	void execute(Machine machine) {
		Object[] arguments = new Object[length];
		for (int index = 0; index < length; index++) {
			arguments[index] = machine.operands.pop();
		}
		try {
			machine.operands.push(constructor.newInstance(arguments));
		} catch (InstantiationException e) {
			machine.error(e);
		} catch (IllegalAccessException e) {
			machine.error(e);
		} catch (InvocationTargetException e) {
			machine.error(e);
		}
	}

	@Override
	public String toString() {
		return "CONS " + constructor.getName();
	}

}
