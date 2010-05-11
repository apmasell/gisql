package ca.wlu.gisql.function.comparisons;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionRunner;

public class GenomicSimilarityScore extends Function {
	public static double compute(Interactome left, Interactome right) {
		if (!left.prepare() || !right.prepare()) {
			return 0;
		}

		int leftcount = 0;
		int rightcount = 0;
		int sharedcount = 0;
		for (Gene gene : Ubergraph.getInstance().genes()) {
			boolean inleft = Membership.isPresent(left
					.calculateMembership(gene));
			boolean inright = Membership.isPresent(right
					.calculateMembership(gene));
			if (inleft) {
				leftcount++;
			}
			if (inright) {
				rightcount++;
			}
			if (inleft && inright) {
				sharedcount++;
			}
		}
		left.postpare();
		right.postpare();
		return sharedcount * 1.0 / Math.min(leftcount, rightcount);
	}

	public GenomicSimilarityScore(ExpressionRunner runner) {
		super(runner, "gss",
				"Computes the genomic similarity score of two interactomes.",
				Type.InteractomeType, Type.InteractomeType, Type.MembershipType);
	}

	@Override
	public Object run(Object... parameters) {
		Interactome left = (Interactome) parameters[0];
		Interactome right = (Interactome) parameters[1];
		return compute(left, right);
	}

}
