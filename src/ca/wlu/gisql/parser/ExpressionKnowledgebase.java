package ca.wlu.gisql.parser;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.function.list.Join;
import ca.wlu.gisql.interactome.functions.Cut;
import ca.wlu.gisql.interactome.logic.Complement;
import ca.wlu.gisql.interactome.logic.Difference;
import ca.wlu.gisql.interactome.logic.Intersection;
import ca.wlu.gisql.interactome.logic.Residuum;
import ca.wlu.gisql.interactome.logic.SymmetricDifference;
import ca.wlu.gisql.interactome.logic.Union;
import ca.wlu.gisql.interactome.output.AbstractOutput;
import ca.wlu.gisql.parser.descriptors.BracketedExpressionDescriptor;
import ca.wlu.gisql.parser.descriptors.LiteralTokenDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.AssignmentDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.ColonOrderDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.EmptyList;
import ca.wlu.gisql.parser.descriptors.ast.EnumerationDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.FixedPointDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.GraphDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.HelpDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.IfDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.ImportFile;
import ca.wlu.gisql.parser.descriptors.ast.InteractomeDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.LambdaDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.ListFromFile;
import ca.wlu.gisql.parser.descriptors.ast.LiteralList;
import ca.wlu.gisql.parser.descriptors.ast.NegativeDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.PairDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.RecursiveFunctionDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.TypeCheckDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.TypeOfDescriptor;
import ca.wlu.gisql.parser.descriptors.ast.UnitDescriptor;
import ca.wlu.gisql.parser.util.ComputedInteractomeDescriptor;
import ca.wlu.gisql.util.Precedence;

/**
 * This a collection of {@link Parseable}s for the {@link Parser} to find. This
 * class has the core syntax, but new syntax may be added to instances of this
 * class.
 */
public class ExpressionKnowledgebase extends
		ParserKnowledgebase<AstNode, Precedence> {

	private final List<ComputedInteractomeDescriptor> computedInteractomeParsers = new ArrayList<ComputedInteractomeDescriptor>();

	public ExpressionKnowledgebase() {
		super(
				Precedence.values(),
				"Any other word will be interpreted as a identifier for a species or variable.\nFor a list of functions, type \"ls()\".");
		installOperator(AbstractOutput.descriptor);
		installOperator(BracketedExpressionDescriptor.descriptor);
		installOperator(ColonOrderDescriptor.descriptor);
		installOperator(Complement.descriptor);
		installOperator(Cut.descriptor);
		installOperator(Difference.descriptor);
		installOperator(EmptyList.descriptor);
		installOperator(EnumerationDescriptor.descriptor);
		installOperator(FixedPointDescriptor.descriptor);
		installOperator(GraphDescriptor.descriptor);
		installOperator(IfDescriptor.descriptor);
		installOperator(ImportFile.descriptor);
		installOperator(Intersection.descriptor);
		installOperator(HelpDescriptor.descriptor);
		installOperator(InteractomeDescriptor.descriptor);
		installOperator(Join.descriptor);
		installOperator(LambdaDescriptor.descriptor);
		installOperator(ListFromFile.descriptor);
		installOperator(LiteralList.descriptor);
		installOperator(NegativeDescriptor.numberdescriptor);
		installOperator(NegativeDescriptor.realdescriptor);
		installOperator(RecursiveFunctionDescriptor.self);
		installOperator(Residuum.descriptor);
		installOperator(PairDescriptor.descriptor);
		installOperator(SymmetricDifference.descriptor);
		installOperator(AssignmentDescriptor.descriptor);
		installOperator(TypeCheckDescriptor.descriptor);
		installOperator(TypeOfDescriptor.descriptor);
		installOperator(Union.descriptor);
		installOperator(UnitDescriptor.descriptor);

		installOperator(new LiteralTokenDescriptor<AstNode, Precedence>(
				TokenName.<AstNode, Precedence> get(), Precedence.Value));
		installOperator(new LiteralTokenDescriptor<AstNode, Precedence>(
				TokenReal.self, Precedence.Value));
		installOperator(new LiteralTokenDescriptor<AstNode, Precedence>(
				TokenNumber.self, Precedence.Value));
		installOperator(new LiteralTokenDescriptor<AstNode, Precedence>(
				TokenQuotedString.self, Precedence.Value));

		buildHelp();
	}

	public List<ComputedInteractomeDescriptor> getComputedInteractomeParsers() {
		return computedInteractomeParsers;
	}

	@Override
	public void installOperator(Parseable<AstNode, Precedence> operator) {
		super.installOperator(operator);
		if (operator instanceof ComputedInteractomeDescriptor) {
			computedInteractomeParsers
					.add((ComputedInteractomeDescriptor) operator);
		}
	}

	@Override
	AstNode makeApplication(Parser parser, AstNode left, AstNode right) {
		return new AstApplication(left, right);
	}

	@Override
	AstNode makeBoolean(boolean value) {
		return new AstLiteral(Type.BooleanType, value);
	}

	@Override
	AstNode makeName(Parser parser, String name) {
		return new AstName(name);
	}
}
