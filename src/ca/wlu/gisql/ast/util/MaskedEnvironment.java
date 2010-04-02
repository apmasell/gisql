package ca.wlu.gisql.ast.util;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.Environment;

public class MaskedEnvironment<T extends AstNode & NamedVariable> implements
		ResolutionEnvironment {
	private final ResolutionEnvironment parent;
	private boolean used = false;
	private final T variable;

	public MaskedEnvironment(T variable, ResolutionEnvironment parent) {
		this.variable = variable;
		this.parent = parent;
	}

	@Override
	public Environment getEnvironment() {
		return parent.getEnvironment();
	}

	public T getVariable() {
		return variable;
	}

	public boolean isUsed() {
		return used;
	}

	@Override
	public AstNode lookup(String name) {
		if (name.equals(variable.getVariableName())) {
			used = true;
			return variable;
		} else {
			return parent.lookup(name);
		}
	}
}