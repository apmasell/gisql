package ca.wlu.gisql.environment.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.wlu.gisql.environment.ClearFunction;
import ca.wlu.gisql.environment.EchoFunction;
import ca.wlu.gisql.environment.FormatFunction;
import ca.wlu.gisql.environment.LastInteractome;
import ca.wlu.gisql.environment.OutputFunction;
import ca.wlu.gisql.environment.RunFunction;
import ca.wlu.gisql.environment.parser.util.ComputedInteractomeParser;
import ca.wlu.gisql.environment.parser.util.FoldOperator;
import ca.wlu.gisql.interactome.Complement;
import ca.wlu.gisql.interactome.Cut;
import ca.wlu.gisql.interactome.Patch;
import ca.wlu.gisql.interactome.ToVar;
import ca.wlu.gisql.interactome.binary.Difference;
import ca.wlu.gisql.interactome.binary.Intersection;
import ca.wlu.gisql.interactome.binary.Residuum;
import ca.wlu.gisql.interactome.binary.SymmetricDifference;
import ca.wlu.gisql.interactome.binary.Union;
import ca.wlu.gisql.interactome.output.AbstractOutput;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class ParserKnowledgebase {

	private static Map<Integer, List<Parseable>> otherfixOperators = null;

	private String help;

	int maxdepth = 0;

	private Map<Integer, List<Parseable>> prefixedOperators = null;

	public ParserKnowledgebase() {
		prefixedOperators = new HashMap<Integer, List<Parseable>>();
		otherfixOperators = new HashMap<Integer, List<Parseable>>();

		installOperator(AbstractOutput.descriptor);
		installOperator(ClearFunction.descriptor);
		installOperator(Complement.descriptor);
		installOperator(Cut.descriptor);
		installOperator(Difference.descriptor);
		installOperator(EchoFunction.descriptor);
		installOperator(FormatFunction.descriptor);
		installOperator(Intersection.descriptor);
		installOperator(LastInteractome.descriptor);
		installOperator(OutputFunction.descriptor);
		installOperator(Patch.descriptor);
		installOperator(Residuum.descriptor);
		installOperator(RunFunction.descriptor);
		installOperator(SymmetricDifference.descriptor);
		installOperator(ToVar.descriptor);
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
		print.append("Lists may be any of the following:\n");
		ListExpression.show(print);
		print.println();
		print
				.println("Any other word will be interpreted as a identifier for a species or variable.");
		print
				.println("Parentheses may be used to control order of operations.");
		help = print.toString();

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

	public List<Parseable> getOtherfix(int level) {
		return otherfixOperators.get(level);
	}

	public List<Parseable> getPrefix(int level) {
		return prefixedOperators.get(level);
	}

	private void installOperator(Parseable operator) {
		int level = operator.getPrecedence();
		if (level > maxdepth)
			maxdepth = level;
		List<Parseable> list = getList(
				(operator.isPrefixed() ? prefixedOperators : otherfixOperators),
				level);
		if (list.contains(operator))
			return;
		list.add(operator);
		if (operator instanceof ComputedInteractomeParser) {
			getList(prefixedOperators, level).add(
					new FoldOperator((ComputedInteractomeParser) operator));
		}
	}

}