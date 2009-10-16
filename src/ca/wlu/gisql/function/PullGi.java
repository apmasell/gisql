package ca.wlu.gisql.function;

import java.util.ArrayList;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.vm.Machine;

public class PullGi extends Function {
	public static final Function self = new PullGi();

	private PullGi() {
		super("gi", "Get the genes for a particular gene identifier.",
				Type.NumberType, new ListType(Type.GeneType));
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		return new ArrayList<Gene>(Ubergraph.getInstance().findGenes(
				(Long) parameters[0]));
	}
}
