package ca.wlu.gisql.graph.biologicalfunctions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ca.wlu.gisql.graph.BiologicalFunction;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

/**
 * Represents a Cluster of Orthologous Genes-assigned function. Each COG is
 * represented by a single character.
 */
public class Cog implements BiologicalFunction {
	private static final Map<Character, Cog> library = new HashMap<Character, Cog>();

	public static Cog makeCog(Character id) {
		Cog cog = library.get(id);
		if (cog == null) {
			cog = new Cog(id);
			library.put(id, cog);
			Ubergraph.add(cog);
		}
		return cog;
	}

	private final Character id;

	private Cog(Character id) {
		super();
		this.id = id;
	}

	public double compare(BiologicalFunction other) {
		return other == this ? 1 : 0;
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print("COG: ");
		print.print(id);
	}

	@Override
	public String toString() {
		return ShowableStringBuilder.toString(this, null);
	}
}
