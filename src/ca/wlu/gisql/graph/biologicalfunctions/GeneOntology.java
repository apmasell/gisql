package ca.wlu.gisql.graph.biologicalfunctions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ca.wlu.gisql.graph.BiologicalFunction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;
import ca.wlu.gisql.util.multimap.BackedMultiMap;
import ca.wlu.gisql.util.multimap.EmptyMultiMap;
import ca.wlu.gisql.util.multimap.SimpleMultiMap;

/**
 * Represents a Gene Ontoloy (GO)-assigned function. The GO term heirarchy must
 * be provided to make comparisons.
 */
public class GeneOntology implements BiologicalFunction {

	private static final SimpleMultiMap hierarchy;

	private static final Map<Integer, GeneOntology> library = new HashMap<Integer, GeneOntology>();
	private static final Logger log = Logger.getLogger(GeneOntology.class);
	static {
		SimpleMultiMap map = null;
		try {
			map = new BackedMultiMap("goterms.bin");
		} catch (IOException e) {
			log.error("Failed to initialise gene ontology database.", e);
			map = EmptyMultiMap.self;
		}
		hierarchy = map;
	}

	public static GeneOntology makeGO(int identifier) {
		GeneOntology go = library.get(identifier);
		if (go == null) {
			go = new GeneOntology(identifier);
			library.put(identifier, go);
		}
		return go;
	}

	private final int identfier;

	private GeneOntology(final int identfier) {
		super();
		this.identfier = identfier;
	}

	public double compare(BiologicalFunction other) {
		if (this == other) {
			return 1;
		} else if (other instanceof GeneOntology) {
			GeneOntology othergo = (GeneOntology) other;
			int distance = hierarchy.distanceBetween(identfier,
					othergo.identfier);
			if (distance == -1) {
				return 0;
			} else {
				return 1.0 / distance;
			}
		} else {
			return 0;
		}
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print("GO:");
		print.print(identfier);
	}

	@Override
	public String toString() {
		return ShowableStringBuilder.toString(this, null);
	}
}
