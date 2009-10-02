package ca.wlu.gisql.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.wlu.gisql.environment.functions.LastInteractome;
import ca.wlu.gisql.interactome.cut.Cut;
import ca.wlu.gisql.interactome.logic.Complement;
import ca.wlu.gisql.interactome.logic.Difference;
import ca.wlu.gisql.interactome.logic.Intersection;
import ca.wlu.gisql.interactome.logic.Residuum;
import ca.wlu.gisql.interactome.logic.SymmetricDifference;
import ca.wlu.gisql.interactome.logic.Union;
import ca.wlu.gisql.interactome.output.AbstractOutput;
import ca.wlu.gisql.interactome.tovar.ToVar;
import ca.wlu.gisql.parser.util.ComputedInteractomeParser;
import ca.wlu.gisql.parser.util.HelpDescriptor;
import ca.wlu.gisql.parser.util.LambdaDescriptor;
import ca.wlu.gisql.parser.util.LiteralList;
import ca.wlu.gisql.parser.util.TypeOfDescriptor;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class ParserKnowledgebase {

	private final List<ComputedInteractomeParser> computedInteractomeParsers = new ArrayList<ComputedInteractomeParser>();

	private String help;

	int maxdepth = 0;

	private final Map<Integer, List<Parseable>> otherfixOperators = new HashMap<Integer, List<Parseable>>();
	private final Map<Integer, List<Parseable>> prefixedOperators = new HashMap<Integer, List<Parseable>>();

	public ParserKnowledgebase() {
		installOperator(AbstractOutput.descriptor);
		installOperator(Complement.descriptor);
		installOperator(Cut.descriptor);
		installOperator(Difference.descriptor);
		installOperator(Intersection.descriptor);
		installOperator(HelpDescriptor.descriptor);
		installOperator(LambdaDescriptor.descriptor);
		installOperator(LastInteractome.descriptor);
		installOperator(LiteralList.descriptor);
		installOperator(Residuum.descriptor);
		installOperator(SymmetricDifference.descriptor);
		installOperator(ToVar.descriptor);
		installOperator(TypeOfDescriptor.descriptor);
		installOperator(Union.descriptor);

		buildHelp();
	}

	public synchronized void addParseable(Parseable operator) {
		installOperator(operator);
		buildHelp();
	}

	private void buildHelp() {
		ShowableStringBuilder<ParserKnowledgebase> print = new ShowableStringBuilder<ParserKnowledgebase>(
				this);
		print.println("Syntax Help");
		print.println();
		print
				.println("Each operator and it's membership function is described from lowest precedence to highest.");
		print.println();
		/* This also initialises every entry in the maps. */
		for (int level = 0; level <= maxdepth; level++) {
			for (Parseable operator : getList(prefixedOperators, level)) {
				print.println(operator);
			}
			for (Parseable operator : getList(otherfixOperators, level)) {
				print.println(operator);
			}
			print.println();

		}
		print
				.println("Any other word will be interpreted as a identifier for a species or variable.");
		print
				.println("Parentheses may be used to control order of operations.");
		help = print.toString();

	}

	public List<ComputedInteractomeParser> getComputedInteractomeParsers() {
		return computedInteractomeParsers;
	}

	public String getHelp() {
		return help;
	}

	private synchronized List<Parseable> getList(
			Map<Integer, List<Parseable>> map, int level) {
		List<Parseable> list = map.get(level);
		if (list == null) {
			list = new ArrayList<Parseable>();
			map.put(level, list);
		}
		return list;
	}

	protected List<Parseable> getOtherfix(int level) {
		return otherfixOperators.get(level);
	}

	protected List<Parseable> getPrefix(int level) {
		return prefixedOperators.get(level);
	}

	private void installOperator(Parseable operator) {
		int level = operator.getPrecedence();
		if (level > maxdepth) {
			maxdepth = level;
		}
		List<Parseable> list = getList(
				(operator.isPrefixed() ? prefixedOperators : otherfixOperators),
				level);
		if (list.contains(operator)) {
			return;
		}
		list.add(operator);
		if (operator instanceof ComputedInteractomeParser) {
			computedInteractomeParsers
					.add((ComputedInteractomeParser) operator);
		}
	}

}
