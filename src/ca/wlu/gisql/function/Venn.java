package ca.wlu.gisql.function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.logic.ComputedInteractome;
import ca.wlu.gisql.interactome.metrics.MetricsInteractome;
import ca.wlu.gisql.interactome.metrics.Totals;
import ca.wlu.gisql.vm.Machine;

public class Venn extends Function {

	public static final Function self = new Venn();

	private Venn() {
		super("venn",
				"Create a Venn diagram (true = genes or false= interactions)",
				new ListType(Type.InteractomeType), Type.BooleanType,
				Type.StringType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run(Machine machine, Object... parameters) {
		List<Interactome> interactomes = (List<Interactome>) parameters[0];
		boolean genes = (Boolean) parameters[1];

		int total = 1 << interactomes.size();
		StringBuilder sb = new StringBuilder();

		for (int vennstate = 1; vennstate < total; vennstate++) {
			Totals totals = new Totals();
			List<List<Integer>> productOfSums = new ArrayList<List<Integer>>();
			List<List<Integer>> productOfSumsNegated = new ArrayList<List<Integer>>();

			for (int bit = 0; bit < interactomes.size(); bit++) {
				boolean negated = (vennstate & 1 << bit) == 0;
				(negated ? productOfSumsNegated : productOfSums)
						.add(Collections.singletonList(bit));
				(negated ? productOfSums : productOfSumsNegated)
						.add(Collections.EMPTY_LIST);
			}

			MetricsInteractome metrics = new MetricsInteractome(
					new ComputedInteractome(interactomes, productOfSums,
							productOfSumsNegated), totals);
			if (!metrics.process()) {
				return "";
			}

			String number = Integer.toBinaryString(vennstate);
			sb.append("venn");
			for (int i = number.length(); i < interactomes.size(); i++) {
				sb.append('0');
			}
			sb.append(number);
			sb.append(" = ");
			sb.append(genes ? totals.getGeneCount() : totals
					.getInteractionCount());
			sb.append("; ");
		}
		return sb.toString();
	}

}
