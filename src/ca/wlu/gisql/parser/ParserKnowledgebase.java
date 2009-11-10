package ca.wlu.gisql.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;

import ca.wlu.gisql.environment.functions.LastInteractome;
import ca.wlu.gisql.function.list.Cons;
import ca.wlu.gisql.function.list.Join;
import ca.wlu.gisql.interactome.cut.Cut;
import ca.wlu.gisql.interactome.logic.Complement;
import ca.wlu.gisql.interactome.logic.Difference;
import ca.wlu.gisql.interactome.logic.Intersection;
import ca.wlu.gisql.interactome.logic.Residuum;
import ca.wlu.gisql.interactome.logic.SymmetricDifference;
import ca.wlu.gisql.interactome.logic.Union;
import ca.wlu.gisql.interactome.output.AbstractOutput;
import ca.wlu.gisql.parser.descriptors.BracketedExpressionDescriptor;
import ca.wlu.gisql.parser.descriptors.ColonOrderDescriptor;
import ca.wlu.gisql.parser.descriptors.EmptyList;
import ca.wlu.gisql.parser.descriptors.HelpDescriptor;
import ca.wlu.gisql.parser.descriptors.LambdaDescriptor;
import ca.wlu.gisql.parser.descriptors.LiteralList;
import ca.wlu.gisql.parser.descriptors.LiteralTokenDescriptor;
import ca.wlu.gisql.parser.descriptors.ToVarDescriptor;
import ca.wlu.gisql.parser.descriptors.TypeOfDescriptor;
import ca.wlu.gisql.parser.descriptors.UnitDescriptor;
import ca.wlu.gisql.parser.util.ComputedInteractomeDescriptor;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowableStringBuilder;

/**
 * This a collection of {@link Parseable}s for the {@link Parser} to find. This
 * class has the core syntax, but new syntax may be added to instances of this
 * class.
 */
public class ParserKnowledgebase {

	private final List<ComputedInteractomeDescriptor> computedInteractomeParsers = new ArrayList<ComputedInteractomeDescriptor>();

	private String help;

	private final OrderedMap<Precedence, List<Parseable>> operators = new ListOrderedMap<Precedence, List<Parseable>>();

	public ParserKnowledgebase() {
		installOperator(AbstractOutput.descriptor);
		installOperator(BracketedExpressionDescriptor.descriptor);
		installOperator(ColonOrderDescriptor.descriptor);
		installOperator(Cons.descriptor);
		installOperator(Complement.descriptor);
		installOperator(Cut.descriptor);
		installOperator(Difference.descriptor);
		installOperator(EmptyList.descriptor);
		installOperator(Intersection.descriptor);
		installOperator(HelpDescriptor.descriptor);
		installOperator(Join.descriptor);
		installOperator(LambdaDescriptor.descriptor);
		installOperator(LastInteractome.descriptor);
		installOperator(LiteralList.descriptor);
		installOperator(Residuum.descriptor);
		installOperator(SymmetricDifference.descriptor);
		installOperator(ToVarDescriptor.self);
		installOperator(TypeOfDescriptor.descriptor);
		installOperator(Union.descriptor);
		installOperator(UnitDescriptor.descriptor);

		installOperator(new LiteralTokenDescriptor(TokenName.self));
		installOperator(new LiteralTokenDescriptor(TokenReal.self));
		installOperator(new LiteralTokenDescriptor(TokenNumber.self));
		installOperator(new LiteralTokenDescriptor(TokenQuotedString.self));

		buildHelp();
	}

	/** Add a new syntax element to the current language. */
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
		for (Precedence level : Precedence.values()) {
			for (Parseable operator : getList(operators, level)) {
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

	public List<ComputedInteractomeDescriptor> getComputedInteractomeParsers() {
		return computedInteractomeParsers;
	}

	/** Provide help text collected from the {@link Parseable}s. */
	public String getHelp() {
		return help;
	}

	private synchronized List<Parseable> getList(
			Map<Precedence, List<Parseable>> map, Precedence level) {
		List<Parseable> list = map.get(level);
		if (list == null) {
			list = new ArrayList<Parseable>();
			map.put(level, list);
		}
		return list;
	}

	protected List<Parseable> getOperators(Precedence level) {
		return operators.get(level);
	}

	private void installOperator(Parseable operator) {
		List<Parseable> list = getList(operators, operator.getPrecedence());
		if (list.contains(operator)) {
			return;
		}
		list.add(operator);
		if (operator instanceof ComputedInteractomeDescriptor) {
			computedInteractomeParsers
					.add((ComputedInteractomeDescriptor) operator);
		}
	}

}
