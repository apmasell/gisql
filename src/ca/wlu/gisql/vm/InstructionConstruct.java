package ca.wlu.gisql.vm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Call a Java constructor, popping an appropriate number of objects from the
 * operand stack and pushing the newly created Java object on the stack.
 */
public class InstructionConstruct extends Instruction {

	private final Constructor<?> constructor;
	private final int length;
	/**
	 * Some Java objects need a copy of the current {@link Machine}. This will
	 * determine if the first parameters should be a duplicate of the current
	 * machine.
	 */
	private final boolean needsmachine;

	public InstructionConstruct(Constructor<?> constructor) {
		this.constructor = constructor;
		Class<?>[] parameters = constructor.getParameterTypes();
		length = parameters.length;
		needsmachine = Machine.class.isAssignableFrom(parameters[0]);
	}

	@Override
	void execute(Machine machine) {
		Object[] arguments = new Object[length];
		if (needsmachine) {
			arguments[0] = machine.duplicate();
		}
		for (int index = needsmachine ? 1 : 0; index < length; index++) {
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
