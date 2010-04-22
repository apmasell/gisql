package ca.wlu.gisql.function;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionRunner;

public class PhylipOutput extends Function {
	private static final Logger log = Logger.getLogger(PhylipOutput.class);

	public PhylipOutput(ExpressionRunner runner) {
		super(runner, "phylip", "produces phylip based phylogenetic tree",
				Type.StringType, new ListType(Type.InteractomeType),
				Type.StringType);
	}

	private void addname(int i, StringBuilder line) {
		if (i == 0) {
			line.append('A');
		}
		while (i > 0) {
			char digit = (char) ('A' + i % 26);
			line.append(digit);
			i = i / 26;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run(Object... parameters) {
		String filename = (String) parameters[0];
		List<Interactome> interactomeList = (List<Interactome>) parameters[1];
		Map<Interactome, StringBuilder> builders = new HashMap<Interactome, StringBuilder>();
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < interactomeList.size(); i++) {
			Interactome interactome = interactomeList.get(i);
			StringBuilder line = new StringBuilder();
			builders.put(interactome, line);
			addname(i, line);
			while (line.length() < 10) {
				line.append(" ");
			}

			addname(i, output);
			output.append("=");
			output.append(interactome);
			output.append("\n");
			if (!interactome.prepare()) {
				return "";
			}
		}

		for (Gene gene : Ubergraph.getInstance().genes()) {
			for (Interactome interactome : interactomeList) {
				interactome.calculateMembership(gene);
			}
		}
		int characters = 0;

		for (Interaction interaction : Ubergraph.getInstance()) {
			Boolean state = null;
			for (int index = 0; index < interactomeList.size(); index++) {
				Interactome interactome = interactomeList.get(index);
				boolean membership = Membership.isPresent(interactome
						.calculateMembership(interaction));
				if (index == 0) {
					state = membership;
				} else if (state == null) {
					builders.get(interactome).append(bitValue(membership));
				} else if (state != membership) {
					char c = bitValue(state);
					for (int old = 0; old < index; old++) {
						builders.get(interactomeList.get(old)).append(c);
					}
					builders.get(interactome).append(bitValue(membership));
					state = null;
					characters++;
				}
			}
		}

		try {
			FileWriter out = new FileWriter(filename);
			out.write(Integer.toString(interactomeList.size()));
			out.write(" ");
			out.write(Integer.toString(characters));
			out.write("\n");

			for (StringBuilder stringBuilder : builders.values()) {
				out.write(stringBuilder.toString());
				out.write("\n");
			}
			out.close();
		} catch (IOException exception) {
			log.error("Could not create Phylip file", exception);
		}
		for (Interactome interactome : interactomeList) {
			interactome.postpare();
		}
		return output.toString();
	}

	private char bitValue(Boolean state) {
		return state ? '1' : '0';
	}
}
