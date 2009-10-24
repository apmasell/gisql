package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.environment.ParserEnvironment;
import ca.wlu.gisql.vm.InstructionFunction;
import ca.wlu.gisql.vm.Machine;

/**
 * Allows an arbitrary Java function to be come part of the query language.
 * Simply subclass this and add an instance to {@link ParserEnvironment}.
 */
public abstract class Function extends AstNative {
	public Function(String name, String description, Type... types) {
		super(name, description, types);
	}

	@Override
	public final boolean render(ProgramRoutine program, int depth, int debrujin) {
		if (depth >= types.length - 1) {
			return program.instructions.add(new InstructionFunction(this));
		} else {
			return wrap(program, depth, debrujin);
		}
	}

	/**
	 * This method is called by the query language.
	 * 
	 * @param machine
	 *            is the machine invoking this function.
	 * @param parameters
	 *            are the parameters passed by the query language. Type checking
	 *            as ensure that they are of the correct type. This function
	 *            should not return null; to return nothing, return
	 *            {@link Unit#nil}.
	 */
	public abstract Object run(Machine machine, Object... parameters);

}
