package ca.wlu.gisql.functions;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.parser.ast.AstLogic;
import ca.wlu.gisql.interactome.metrics.Cardinality;
import ca.wlu.gisql.interactome.metrics.MetricsInteractome;

public class ModifiedJaccardScore implements ScoringFunction<Tree> {

	private static final Logger log = Logger
			.getLogger(ModifiedJaccardScore.class);
	public final static ModifiedJaccardScore self = new ModifiedJaccardScore();

	private Map<Tree, Double> unionValues = new WeakHashMap<Tree, Double>();

	private ModifiedJaccardScore() {
		super();
	}

	public double computeScore(Tree left, Tree right) {
		Cardinality intersectionSize = new Cardinality();
		MetricsInteractome intersection = new MetricsInteractome(AstLogic
				.makeConjunct(left.getIntersection(), right.getIntersection())
				.asInteractome(), intersectionSize);

		log.info(intersection.toString());
		boolean intersectionResult = intersection.process();
		if (intersectionResult) {
			return 1
					- intersectionSize.getInteractionSize()
					/ Math.min(computeUnionValue(left),
							computeUnionValue(right));
		} else {
			return GisQL.Missing;
		}
	}

	private double computeUnionValue(Tree tree) {
		Double value = unionValues.get(tree);
		if (value == null) {
			Cardinality size = new Cardinality();
			MetricsInteractome union = new MetricsInteractome(tree.getUnion()
					.asInteractome(), size);
			log.info(union.toString());
			if (union.process()) {
				value = size.getInteractionSize();
			} else {
				value = GisQL.Missing;
			}
			unionValues.put(tree, value);
		}
		return value;
	}
}
