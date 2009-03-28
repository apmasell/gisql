package ca.wlu.gisql.interactome;

import java.util.Iterator;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interaction.CompositeInteraction;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.interaction.TranslatedInteraction;

public abstract class ArithmeticInteractome extends Interactome {
	static final Logger log = Logger.getLogger(ArithmeticInteractome.class);

	protected Interactome i1, i2;

	protected String symbol = "?";

	public ArithmeticInteractome(Interactome left, Interactome right) {
		i1 = left;
		i2 = right;
	}

	protected abstract double calculateMembership(Interaction j1, Interaction j2);

	public long findOrtholog(long gene) {
		/*
		 * We seek to put orthlogs in the reference point of interactome #1
		 * where possible. If there exists an orthlog in that genome, use it
		 * preferentially.
		 */
		long o1 = i1.findOrtholog(gene);
		return (o1 != -1 ? o1 : i2.findOrtholog(gene));
	}

	protected void prepareInteractions() {
		Iterator<Interaction> itI1 = i1.iterator();
		Iterator<Interaction> itI2 = i2.iterator();

		log.info("Computing left interactions");

		while (itI1.hasNext()) {
			Interaction j1 = itI1.next();
			long g1A = j1.getGene1();
			long g1B = j1.getGene2();

			long g2A = i2.findOrtholog(g1A);
			long g2B = i2.findOrtholog(g1B);

			Interaction j2 = i2.getInteraction(g2A, g2B);
			if (j2 == null) {
				addInteraction(processLoneInteraction(j1, true));
			} else {
				double membership = calculateMembership(j1, j2);
				Interaction i = new CompositeInteraction(this, j1, j2,
						membership);
				addInteraction(i);

			}
		}
		log.info("Computing right interactions");
		while (itI2.hasNext()) {
			Interaction j2 = itI2.next();
			if (i2.getInteraction(j2.getGene1(), j2.getGene2()) == null) {
				long g1A = i2.findOrtholog(j2.getGene1());
				long g1B = i2.findOrtholog(j2.getGene2());
				if (g1A != -1) {
					g1A = j2.getGene1();
				}
				if (g1B != -1) {
					g1B = j2.getGene2();
				}
				j2 = new TranslatedInteraction(i1, j2, g1A, g1B);

				addInteraction(processLoneInteraction(j2, false));
			}
		}
		log.info("Set operation complete");
	}

	protected abstract Interaction processLoneInteraction(Interaction j1,
			boolean left);

	public StringBuilder show(StringBuilder sb) {
		sb.append("(");
		i1.show(sb);
		sb.append(" ").append(symbol).append(" ");
		i2.show(sb);
		sb.append(")");
		return sb;
	}
}