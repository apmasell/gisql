package ca.wlu.gisql.ast.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.ast.AstLambda2;
import ca.wlu.gisql.ast.AstLambdaParameter;
import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstLiteralReference;
import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstNative;
import ca.wlu.gisql.ast.AstNativeConstructor;
import ca.wlu.gisql.ast.AstNativeGenericFunction;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.functions.Comparison;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.functions.EchoFunction;
import ca.wlu.gisql.environment.functions.FormatFunction;
import ca.wlu.gisql.environment.functions.OutputFileFunction;
import ca.wlu.gisql.environment.functions.RunFunction;
import ca.wlu.gisql.environment.functions.SilentFunction;
import ca.wlu.gisql.function.PhylipOutput;
import ca.wlu.gisql.function.PullGi;
import ca.wlu.gisql.function.Range;
import ca.wlu.gisql.function.SearchGenes;
import ca.wlu.gisql.function.Venn;
import ca.wlu.gisql.function.arithmetic.Number2Real;
import ca.wlu.gisql.function.arithmetic.NumberAdd;
import ca.wlu.gisql.function.arithmetic.NumberDivide;
import ca.wlu.gisql.function.arithmetic.NumberModulo;
import ca.wlu.gisql.function.arithmetic.NumberMultiply;
import ca.wlu.gisql.function.arithmetic.NumberSubtract;
import ca.wlu.gisql.function.arithmetic.Real2Number;
import ca.wlu.gisql.function.comparisons.Equal;
import ca.wlu.gisql.function.comparisons.GreaterThan;
import ca.wlu.gisql.function.comparisons.GreaterThanOrEqualTo;
import ca.wlu.gisql.function.comparisons.LessThan;
import ca.wlu.gisql.function.comparisons.LessThanOrEqualTo;
import ca.wlu.gisql.function.comparisons.NotEqual;
import ca.wlu.gisql.function.list.Flatten;
import ca.wlu.gisql.function.list.FoldLeft;
import ca.wlu.gisql.function.list.FoldRight;
import ca.wlu.gisql.function.list.Join;
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
import ca.wlu.gisql.interactome.CompleteInteractome;
import ca.wlu.gisql.interactome.EmptyInteractome;
import ca.wlu.gisql.interactome.coreicity.JaccardCoreicity;
import ca.wlu.gisql.interactome.orphans.Orphans;
import ca.wlu.gisql.interactome.output.FileFormat;
import ca.wlu.gisql.interactome.output.OutputFunction;
import ca.wlu.gisql.interactome.patch.PatchFunction;
import ca.wlu.gisql.interactome.proximity.Proximity;
import ca.wlu.gisql.interactome.snap.Snap;
import ca.wlu.gisql.parser.util.ComputedInteractomeDescriptor;

/**
 * All values and functions that are part of the query language should be
 * defined in this environment. This is used during resolution to find all built
 * in functions.
 */
public class BuiltInResolver implements ResolutionEnvironment {

	private static final java.util.Map<String, AstNode> defaultvalues = new HashMap<String, AstNode>();

	private static final Logger log = Logger.getLogger(Rendering.class);

	static {

		defaultvalues.put("true", new AstLiteral(Type.BooleanType, true));
		defaultvalues.put("false", new AstLiteral(Type.BooleanType, false));
		defaultvalues.put("iinf", new AstLiteral(Type.NumberType,
				Long.MAX_VALUE));
		try {
			defaultvalues.put("null", new AstLiteralReference(
					EmptyInteractome.class.getField("self"),
					Type.InteractomeType));
			defaultvalues.put("universe", new AstLiteralReference(
					CompleteInteractome.class.getField("self"),
					Type.InteractomeType));
		} catch (SecurityException e) {
			log.error("Failed to access field.", e);
		} catch (NoSuchFieldException e) {
			log.error("Failed to access field.", e);
		}

		add(defaultvalues, Type.FormatType, FileFormat.class);

		AstLambdaParameter notparam = new AstLambdaParameter("__not");
		defaultvalues.put("not", new AstLambda2(notparam, AstLogic
				.makeNegation(notparam)));

		addDefault(new Comparison());

		addDefault(EchoFunction.class);
		addDefault(Equal.class);
		addDefault(Flatten.class);
		addDefault(FoldLeft.class);
		addDefault(FoldRight.class);
		addDefault(FormatFunction.class);
		addDefault(GenomeCardinality.class);
		addDefault(GenomeFuzziness.class);
		addDefault(GenomeSize.class);
		addDefault(GreaterThan.class);
		addDefault(GreaterThanOrEqualTo.class);
		addDefault(InteractomeCardinality.class);
		addDefault(InteractomeFuzziness.class);
		addDefault(InteractomeSize.class);
		addDefault(JaccardCoreicity.class);
		addDefault(Join.class);
		addDefault(LessThan.class);
		addDefault(LessThanOrEqualTo.class);
		addDefault(ListFromFile.class);
		addDefault(ListLength.class);
		addDefault(Map.class);
		addDefault(NotEqual.class);
		addDefault(Number2Real.class);
		addDefault(NumberAdd.class);
		addDefault(NumberDivide.class);
		addDefault(NumberModulo.class);
		addDefault(NumberMultiply.class);
		addDefault(NumberSubtract.class);
		addDefault(Orphans.class);
		addDefault(OutputFileFunction.class);
		addDefault(OutputFunction.class);
		addDefault(PatchFunction.class);
		addDefault(PhylipOutput.class);
		addDefault(Proximity.class);
		addDefault(PullGi.class);
		addDefault(Range.class);
		addDefault(Real2Number.class);
		addDefault(RunFunction.class);
		addDefault(SearchGenes.class);
		addDefault(SilentFunction.class);
		addDefault(Slice.class);
		addDefault(Snap.class);
		addDefault(Venn.class);
		addDefault(Zip.class);
	}

	/**
	 * Convenience method to define any {@link AstNative} type using the
	 * built-in name.
	 */
	private static void add(java.util.Map<String, AstNode> lookup,
			AstNative function) {
		lookup.put(function.toString(), function);
	}

	/**
	 * Convenience method to define any class which is decorated with
	 * {@link GisqlConstructorFunction}.
	 */
	private static void add(java.util.Map<String, AstNode> lookup,
			Class<?> clazz) {
		if (Function.class.isAssignableFrom(clazz)) {
			try {
				add(lookup, new AstNativeGenericFunction((Function) clazz
						.getConstructors()[0]
						.newInstance(new Object[] { null })));
			} catch (IllegalArgumentException e) {
				log.error("Failed to access constructor.", e);
			} catch (SecurityException e) {
				log.error("Failed to access constructor.", e);
			} catch (InstantiationException e) {
				log.error("Failed to access constructor.", e);
			} catch (IllegalAccessException e) {
				log.error("Failed to access constructor.", e);
			} catch (InvocationTargetException e) {
				log.error("Failed to access constructor.", e);
			}
		} else if (AstNative.class.isAssignableFrom(clazz)) {
			try {
				add(lookup, (AstNative) clazz.getConstructors()[0]
						.newInstance());
			} catch (IllegalArgumentException e) {
				log.error("Failed to access constructor.", e);
			} catch (SecurityException e) {
				log.error("Failed to access constructor.", e);
			} catch (InstantiationException e) {
				log.error("Failed to access constructor.", e);
			} catch (IllegalAccessException e) {
				log.error("Failed to access constructor.", e);
			} catch (InvocationTargetException e) {
				log.error("Failed to access constructor.", e);
			}
		} else {
			add(lookup, new AstNativeConstructor(clazz));
		}
	}

	private static <E extends Enum<E>> void add(
			java.util.Map<String, AstNode> lookup, Type type,
			Class<E> enumeration) {
		for (Field item : enumeration.getFields()) {
			lookup.put(item.getName(), new AstLiteralReference(item, type));
		}
	}

	private static void addDefault(AstNative function) {
		defaultvalues.put(function.toString(), function);
	}

	private static void addDefault(Class<?> clazz) {
		add(defaultvalues, clazz);
	}

	public static AstNative get(String name) {
		AstNode value = defaultvalues.get(name);
		if (value instanceof AstNative) {
			return (AstNative) value;
		} else {
			return null;
		}
	}

	private final ResolutionEnvironment parent;

	private final java.util.Map<String, AstNode> values = new HashMap<String, AstNode>();

	public BuiltInResolver(ResolutionEnvironment parent) {
		this.parent = parent;

		for (ComputedInteractomeDescriptor operator : parent.getEnvironment()
				.getParserKb().getComputedInteractomeParsers()) {
			values.put(operator.getFunctionName(), operator.getFunction());

		}
	}

	@Override
	public Environment getEnvironment() {
		return parent.getEnvironment();
	}

	@Override
	public AstNode lookup(String name) {
		AstNode value = values.get(name);
		if (value == null) {
			value = defaultvalues.get(name);
		}
		if (value == null) {
			value = parent.lookup(name);
		}
		return value;
	}
}
