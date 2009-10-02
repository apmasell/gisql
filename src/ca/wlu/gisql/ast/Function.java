package ca.wlu.gisql.ast;

import java.util.HashSet;
import java.util.Set;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.vm.InstructionFunction;
import ca.wlu.gisql.vm.Machine;

public abstract class Function extends AstNode {
	private final String description;

	private final String name;
	private final Type[] types;

	public Function(String name, String description, Type... types) {
		super();
		if (types.length < 2) {
			throw new IllegalArgumentException("Need at least two types.");
		}
		this.name = name;
		this.description = description;
		this.types = types;
	}

	public final int getArgumentCount() {
		return types.length - 1;
	}

	public final String getDescription() {
		return description;
	}

	public final String getName() {
		return name;
	}

	@Override
	protected final int getNeededParameterCount() {
		return types.length - 1;
	}

	public final int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	@Override
	public Type getType() {
		Type type = types[types.length - 1];
		Set<TypeVariable> variables = new HashSet<TypeVariable>();
		for (int i = types.length - 2; i >= 0; i--) {
			type = new ArrowType(types[i], type);
			if (types[i] instanceof TypeVariable) {
				variables.add((TypeVariable) types[i]);
			}
		}
		return type;
	}

	@Override
	public final boolean render(ProgramRoutine program, int depth, int debrujin) {
		if (depth >= types.length - 1) {
			return program.instructions.add(new InstructionFunction(this));
		} else {
			return wrap(program, depth, debrujin);
		}
	}

	@Override
	public final AstNode resolve(ExpressionRunner runner,
			ExpressionContext context, Environment environment) {
		return this;
	}

	public abstract Object run(Machine machine, Object... parameters);

	public final void show(ShowablePrintWriter<AstNode> print) {
		print.print(name);
	}

	@Override
	public final boolean type(ExpressionRunner runner, ExpressionContext context) {
		return true;
	}

}
