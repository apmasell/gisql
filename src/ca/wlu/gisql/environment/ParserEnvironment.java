package ca.wlu.gisql.environment;

import ca.wlu.gisql.ast.AstLambda2;
import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstParameter;
import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.functions.ClearFunction;
import ca.wlu.gisql.environment.functions.DefinedFunction;
import ca.wlu.gisql.environment.functions.EchoFunction;
import ca.wlu.gisql.environment.functions.FormatFunction;
import ca.wlu.gisql.environment.functions.OutputFileFunction;
import ca.wlu.gisql.environment.functions.RunFunction;
import ca.wlu.gisql.environment.functions.SilentFunction;
import ca.wlu.gisql.function.Comparison;
import ca.wlu.gisql.function.NumericComparison;
import ca.wlu.gisql.function.Range;
import ca.wlu.gisql.function.list.FoldLeft;
import ca.wlu.gisql.function.list.FoldRight;
import ca.wlu.gisql.function.list.ListFromFile;
import ca.wlu.gisql.function.list.ListLength;
import ca.wlu.gisql.function.list.Map;
import ca.wlu.gisql.function.list.Slice;
import ca.wlu.gisql.function.list.Zip;
import ca.wlu.gisql.function.metrics.GenomeCardinality;
import ca.wlu.gisql.function.metrics.GenomeFuzziness;
import ca.wlu.gisql.function.metrics.GenomeSize;
import ca.wlu.gisql.function.metrics.InteractomeCardinality;
import ca.wlu.gisql.function.metrics.InteractomeFuzziness;
import ca.wlu.gisql.function.metrics.InteractomeSize;
import ca.wlu.gisql.interactome.coreicity.Coreicity;
import ca.wlu.gisql.interactome.coreicity.DeltaCoreicity;
import ca.wlu.gisql.interactome.coreicity.JaccardCoreicity;
import ca.wlu.gisql.interactome.cut.Cut;
import ca.wlu.gisql.interactome.defuzzify.Defuzzify;
import ca.wlu.gisql.interactome.orphans.Orphans;
import ca.wlu.gisql.interactome.output.AbstractOutput;
import ca.wlu.gisql.interactome.output.FileFormat;
import ca.wlu.gisql.interactome.patch.Patch;
import ca.wlu.gisql.interactome.proximity.Proximity;
import ca.wlu.gisql.parser.util.ComputedInteractomeParser;

public class ParserEnvironment extends Environment {
	public static final ParserEnvironment self = new ParserEnvironment(null);

	private ParserEnvironment(Environment parent) {
		super(parent, false, false);

		add("true", new AstLiteral(Type.BooleanType, true));
		add("false", new AstLiteral(Type.BooleanType, false));
		add("iinf", new AstLiteral(Type.NumberType, Long.MAX_VALUE));
		AstParameter not = new AstParameter("__not");
		add("not", new AstLambda2(not, AstLogic.makeNegation(not)));

		add(Type.FormatType, FileFormat.values());

		add(NumericComparison.Eq);
		add(NumericComparison.GE);
		add(NumericComparison.GT);
		add(NumericComparison.LE);
		add(NumericComparison.LT);
		add(NumericComparison.NE);

		add(AbstractOutput.function);
		add(ClearFunction.self);
		add(Comparison.self);
		add(Coreicity.function);
		add(Cut.function);
		add(DefinedFunction.self);
		add(Defuzzify.function);
		add(DeltaCoreicity.function);
		add(EchoFunction.self);
		add(FoldLeft.self);
		add(FoldRight.self);
		add(FormatFunction.self);
		add(GenomeCardinality.self);
		add(GenomeFuzziness.self);
		add(GenomeSize.self);
		add(InteractomeCardinality.self);
		add(InteractomeFuzziness.self);
		add(InteractomeSize.self);
		add(JaccardCoreicity.function);
		add(ListFromFile.function);
		add(ListLength.function);
		add(Map.function);
		add(Orphans.function);
		add(OutputFileFunction.self);
		add(Range.self);
		add(Patch.function);
		add(Proximity.function);
		add(RunFunction.self);
		add(SilentFunction.self);
		add(Slice.self);
		add(Zip.self);

		for (ComputedInteractomeParser operator : getParserKb()
				.getComputedInteractomeParsers()) {
			add(operator.getFunctionName(), operator.getFunction());

		}
	}

	public void add(Function function) {
		add(function.toString(), function);
	}

	private <E extends Enum<E>> void add(Type type, E[] values) {
		for (E item : values) {
			add(item.name(), new AstLiteral(type, item));
		}
	}
}
