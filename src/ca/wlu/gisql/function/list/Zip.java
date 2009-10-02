package ca.wlu.gisql.function.list;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.vm.Machine;
import ca.wlu.gisql.vm.Program;

public class Zip extends Function {
	private static final TypeVariable a = new TypeVariable();
	private static final TypeVariable b = new TypeVariable();
	private static final TypeVariable c = new TypeVariable();
	public static final Function self = new Zip();

	public Zip() {
		super("zip", "Dyadic version of map (a.k.a zipWith)", new ArrowType(a,
				b, c), new ListType(a), new ListType(b), new ListType(c));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Machine machine, Object... parameters) {
		Program function = (Program) parameters[0];
		List leftlist = (List) parameters[1];
		List rightlist = (List) parameters[2];

		List output = new ArrayList();
		int upperbound = Math.min(leftlist.size(), rightlist.size());
		for (int index = 0; index < upperbound; index++) {
			output.add(function.run(machine, leftlist.get(index), rightlist
					.get(index)));

		}
		return output;
	}

}
