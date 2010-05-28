package ca.wlu.gisql.function;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.runner.ExpressionRunner;

public class Coreicity extends Function {

	public Coreicity(ExpressionRunner runner) {
		super(runner, "coreicity", "Get the coreicity of a gene.",
				Type.GeneType, Type.NumberType);
	}

	@Override
	public Object run(Object... parameters) {
		return (long) ((Gene) parameters[0]).getCoreicity();
	}
}
