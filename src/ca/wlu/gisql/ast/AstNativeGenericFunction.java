package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.runner.ExpressionRunner;

/** Represents a function implementing {@link GenericFunction}. */
public class AstNativeGenericFunction extends AstNative {
	private final GenericFunction function;

	public AstNativeGenericFunction(Function function) {
		this(function.toString(), function);
	}

	public AstNativeGenericFunction(String boundname, GenericFunction function) {
		super(boundname, function.getDescription(), function.getType());
		this.function = function;
	}

	/**
	 * Call the <b>first</b> constructor in the provided function with the
	 * required arguments. If the function requires access to the current
	 * environment, it may have {@link ExpressionRunner} as its <b>first</b>
	 * argument.
	 */

	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		try {
			return program.pRg$hO_CreateObject(function.getClass()
					.getConstructors()[0])
					&& program.pPg$hO_BoxArguments(type.getArrowDepth())
					&& program.g_InvokeMethod(GenericFunction.class
							.getDeclaredMethod("run", Object[].class));
		} catch (SecurityException e) {
			return false;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

}
