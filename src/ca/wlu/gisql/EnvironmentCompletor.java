package ca.wlu.gisql;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import jline.Completor;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.BuiltInResolver;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.EnvironmentListener;
import ca.wlu.gisql.environment.UserEnvironment;

/** JLine tab-completion module for {@link Environment}. */
public class EnvironmentCompletor implements Completor, EnvironmentListener {
	private final SortedSet<String> candidates = new TreeSet<String>();

	public EnvironmentCompletor(UserEnvironment environment) {
		environment.addListener(this);
		for (Entry<String, Object> entry : environment) {
			candidates.add(entry.getKey());
		}

		for (Entry<String, AstNode> entry : BuiltInResolver.entries()) {
			candidates.add(entry.getKey());
		}
		candidates.addAll(environment.getParserKb().getReservedWords());
	}

	public void addedEnvironmentVariable(String name, Object value, Type type) {
		candidates.add(name);
	}

	@SuppressWarnings("unchecked")
	public int complete(final String buffer, final int cursor,
			final List candidatelist) {
		String start = buffer == null ? "" : buffer;

		for (String candidate : candidates.tailSet(start)) {

			if (!candidate.startsWith(start)) {
				break;
			}

			int index = candidate.indexOf(" ", cursor);

			if (index != -1) {
				candidate = candidate.substring(0, index + 1);
			}

			candidatelist.add(candidate);
		}
		if (candidatelist.size() == 1) {
			candidatelist.set(0, (String) candidatelist.get(0) + " ");
		}

		return candidatelist.size() == 0 ? -1 : 0;
	}

	public void droppedEnvironmentVariable(String name, Object value, Type type) {
		candidates.remove(name);
	}

	public void lastChanged() {
	}

}
