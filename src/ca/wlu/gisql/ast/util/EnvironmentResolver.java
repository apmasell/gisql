package ca.wlu.gisql.ast.util;

import ca.wlu.gisql.ast.AstEnvironmentLoad;
import ca.wlu.gisql.ast.AstNativeGenericFunction;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.Environment;

/**
 * Resolve items found in an {@link Environment} and create {@link AstNode}s for
 * them.f
 */
public class EnvironmentResolver implements ResolutionEnvironment {

	private final Environment environment;

	public EnvironmentResolver(Environment environment) {
		super();
		this.environment = environment;
	}

	@Override
	public Environment getEnvironment() {
		return environment;
	}

	@Override
	public AstNode lookup(String name) {
		Object value = environment.getVariable(name);
		if (value == null) {
			return null;
		}
		if (value instanceof GenericFunction) {
			return new AstNativeGenericFunction(name, (GenericFunction) value);
		} else {
			return new AstEnvironmentLoad(name, environment.getTypeOf(name));
		}
	}
}
