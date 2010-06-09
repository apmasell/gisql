package ca.wlu.gisql.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;

import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.ShowableStringBuilder;

/**
 * This a collection of {@link Parseable}s for the {@link Parser} to find. This
 * class has the core syntax, but new syntax may be added to instances of this
 * class.
 */
public abstract class ParserKnowledgebase<R, P extends Enum<P> & Nextable<P>> {

	private final String extrahelp;

	private String help;
	private final OrderedMap<P, List<Parseable<R, P>>> operators = new ListOrderedMap<P, List<Parseable<R, P>>>();

	private final Set<String> reservedwords = new HashSet<String>();

	private final P[] values;

	public ParserKnowledgebase(P[] values, String extrahelp) {
		this.values = values;
		this.extrahelp = extrahelp;
	}

	/** Add a new syntax element to the current language. */
	public final synchronized void addParseable(Parseable<R, P> operator) {
		installOperator(operator);
		buildHelp();
	}

	protected final void buildHelp() {
		ShowableStringBuilder<ParserKnowledgebase<R, P>> print = new ShowableStringBuilder<ParserKnowledgebase<R, P>>(
				this);
		print.println("Syntax Help");
		print.println();
		/* This also initialises every entry in the maps. */
		for (P level : values) {
			for (Parseable<R, P> operator : getList(operators, level)) {
				print.print(operator);
			}
			print.println();

		}
		print.println(extrahelp);
		help = print.toString();

	}

	/** Provide help text collected from the {@link Parseable}s. */
	public final String getHelp() {
		return help;
	}

	private final synchronized List<Parseable<R, P>> getList(
			Map<P, List<Parseable<R, P>>> map, P level) {
		List<Parseable<R, P>> list = map.get(level);
		if (list == null) {
			list = new ArrayList<Parseable<R, P>>();
			map.put(level, list);
		}
		return list;
	}

	protected final List<Parseable<R, P>> getOperators(P level) {
		return operators.get(level);
	}

	public final Set<String> getReservedWords() {
		return Collections.unmodifiableSet(reservedwords);
	}

	protected void installOperator(Parseable<R, P> operator) {
		List<Parseable<R, P>> list = getList(operators, operator
				.getPrecedence());
		if (list.contains(operator)) {
			return;
		}
		for (Token<R, P> token : operator) {
			token.addReservedWords(reservedwords);
		}

		list.add(operator);
	}

	public final boolean isReservedWord(String name) {
		return reservedwords.contains(name);
	}

	abstract R makeApplication(Parser parser, R left, R right);

	abstract R makeBoolean(boolean b);

	abstract R makeName(Parser parser, String name);
}
