package ca.wlu.gisql.function.comparisons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionRunner;

public class GenomicSimilarityCluster extends Function {
	public static List<List<Interactome>> compute(
			List<Interactome> interactomes, double cutoff) {

		for (Interactome interactome : interactomes) {
			if (!interactome.prepare()) {
				return Collections.emptyList();
			}
		}
		int[][] count = new int[interactomes.size()][];
		for (int index = 0; index < count.length; index++) {
			count[index] = new int[index + 1];
		}

		boolean in[] = new boolean[interactomes.size()];
		for (Gene gene : Ubergraph.getInstance().genes()) {
			for (int index = 0; index < interactomes.size(); index++) {
				in[index] = Membership.isPresent(interactomes.get(index)
						.calculateMembership(gene));
			}
			for (int index = 0; index < count.length; index++) {
				for (int subindex = 0; subindex < count[index].length; subindex++) {
					if (in[index] && in[subindex]) {
						count[index][subindex]++;
					}
				}
			}
		}

		SimpleGraph<Interactome, DefaultEdge> graph = new SimpleGraph<Interactome, DefaultEdge>(
				DefaultEdge.class);
		for (Interactome interactome : interactomes) {
			interactome.postpare();
			graph.addVertex(interactome);
		}

		for (int index = 0; index < count.length; index++) {
			for (int subindex = 0; subindex < count[index].length - 1; subindex++) {
				if (count[index][subindex]
						* 1.0
						/ Math.min(count[index][index],
								count[subindex][subindex]) >= cutoff) {
					graph.addEdge(interactomes.get(index), interactomes
							.get(subindex));
				}
			}
		}

		List<List<Interactome>> result = new ArrayList<List<Interactome>>();
		for (Set<Interactome> set : new ConnectivityInspector<Interactome, DefaultEdge>(
				graph).connectedSets()) {
			result.add(new ArrayList<Interactome>(set));
		}

		return result;
	}

	public GenomicSimilarityCluster(ExpressionRunner runner) {
		super(
				runner,
				"gsscluster",
				"Cluster a list of interactomes based on genomic similarity, cutting the groups at a specific similarity.",
				new ListType(Type.InteractomeType), Type.MembershipType,
				new ListType(new ListType(Type.InteractomeType)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run(Object... parameters) {
		List<Interactome> interactomes = (List<Interactome>) parameters[0];
		double cutoff = (Double) parameters[1];
		return compute(interactomes, cutoff);
	}
}
