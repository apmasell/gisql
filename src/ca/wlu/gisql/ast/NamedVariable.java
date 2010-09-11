package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.util.ResolutionEnvironment;

public abstract class NamedVariable extends AstNode {
	abstract ResolutionEnvironment createEnvironment(
			ResolutionEnvironment environment);

	public abstract String getVariableName();

}
