package ca.wlu.gisql.ast;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Renderable;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Make formatted strings. This function takes any arguments and runs them
 * through the Java string formatter.
 */
public class AstFormatter extends AstNode {

	private static Renderable replaceNullWithMissing = new Renderable() {

		@Override
		public <C> boolean render(Rendering<C> rendering, int depth) {
			Label label = new Label();
			return rendering.lOhO() && rendering.jump(Opcodes.IFNONNULL, label)
					&& rendering.pO() && rendering.hO("missing")
					&& rendering.mark(label);
		}
	};
	private final Object[] arguments;
	private final String format;
	private final int length;

	private final Type type;

	public AstFormatter(String format) {
		this.format = format;

		int slots = 0;
		for (int index = 0; index < format.length() - 1; index++) {
			if (format.charAt(index) == '%') {
				index++;
				int value = 0;
				while (index < format.length() - 1
						&& Character.isDigit(format.charAt(index))) {
					value = value * 10 + format.charAt(index++) - '0';
				}
				if (value > slots) {
					slots = value;
				}
			}
		}
		length = slots;
		arguments = new Object[length];
		Type type = Type.StringType;
		for (int index = 0; index < arguments.length; index++) {
			arguments[index] = "\\{" + (index + 1) + "}";
			type = new ArrowType(new TypeVariable(), type);
		}
		this.type = type;
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	protected <C> boolean renderSelf(Rendering<C> program, int depth) {
		try {
			return program.hO(format)
					&& program.pPg$hO_BoxArguments(length,
							replaceNullWithMissing)
					&& program.g_InvokeMethod(String.class.getMethod("format",
							String.class, Object[].class));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void resetType() {
		type.reset();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		return this;
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print('"');
		print.format(format, arguments);
		print.print('"');
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return true;
	}

}
