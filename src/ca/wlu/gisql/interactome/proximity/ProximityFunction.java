package ca.wlu.gisql.interactome.proximity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.vm.Machine;

public class ProximityFunction extends Function {

	ProximityFunction() {
		super(
				"near",
				"Find genes with in a specified number of degrees from genes specified by a list of gi numbers. Use iinf for maximum radius.",
				Type.InteractomeType, new ListType(Type.NumberType),
				Type.NumberType, Type.InteractomeType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Machine machine, Object... parameters) {
		Interactome source = (Interactome) parameters[0];
		Set<Long> accessions = new HashSet<Long>((List<Long>) parameters[1]);
		long radius = (Long) parameters[1];
		return new Proximity(source, radius, accessions);
	}

}
