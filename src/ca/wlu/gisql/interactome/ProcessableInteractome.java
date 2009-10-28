package ca.wlu.gisql.interactome;

import org.apache.log4j.Logger;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.graph.Ubergraph;

/** An interactome that can have all the genes and edges visited once. */
public abstract class ProcessableInteractome implements Interactome {
	private static final Logger log = Logger
			.getLogger(ProcessableInteractome.class);

	private boolean first = true;

	public boolean postpare() {
		first = false;
		return true;
	}

	/**
	 * Visit all genes and edges.
	 * 
	 * @return false if there is a failure.
	 */
	public final boolean process() {
		if (first) {
			if (!prepare()) {
				log.error("Preparation failed.");
				return false;
			}
			for (Gene gene : Ubergraph.getInstance().genes()) {
				this.calculateMembership(gene);
			}
			for (Interaction interaction : Ubergraph.getInstance()) {
				this.calculateMembership(interaction);
			}
			if (!postpare()) {
				log.error("Postparation failed.");
				return false;
			}
		}
		return true;

	}
}
