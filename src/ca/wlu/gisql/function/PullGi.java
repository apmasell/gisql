package ca.wlu.gisql.function;

import java.util.ArrayList;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.runner.ExpressionRunner;

public class PullGi extends Function {

	public PullGi(ExpressionRunner runner) {
		super(runner, "gi", "Get the genes for a particular gene identifier.",
				Type.NumberType, new ListType(Type.GeneType));
	}

	@Override
	public Object run(Object... parameters) {
		return new ArrayList<Gene>(Ubergraph.getInstance().findGenes(
				(Long) parameters[0]));
	}
}
