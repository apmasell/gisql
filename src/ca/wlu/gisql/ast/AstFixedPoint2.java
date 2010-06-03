package ca.wlu.gisql.ast;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.RenderingFunction;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Phase 2 representation of a fixed-point expression where the parameter is
 * encapsulated as an {@link AstFixedPointParameter}.
 */
public class AstFixedPoint2 extends AstNode {

	private static final Logger log = Logger.getLogger(AstFixedPoint2.class);

	private final AstNode expression;

	private final AstFixedPointParameter self;

	public AstFixedPoint2(AstFixedPointParameter self, AstNode expression) {
		this.self = self;
		this.expression = expression;
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		expression.freeVariables(variables);
		variables.remove(self.variableInformation);
	}

	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	@Override
	public Type getType() {
		return self.getType();
	}

	/**
	 * A fixed-point expression makes a copy of itself available as a variable
	 * on the stack.
	 */
	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		String command = expression.toString();
		Type[] parameters = getType().getParameters();
		Rendering<GenericFunction> subroutine = new RenderingFunction(command,
				getType(), parameters);

		ListOrderedSet<VariableInformation> freevars = this.freeVariables();
		try {
			return subroutine.gF$_CreateFields(freevars.asList())
					&& subroutine.hR_CreateSelfReference(self.name)
					&& expression.render(subroutine, depth)
					&& program.hO_CreateSubroutine(subroutine)
					&& subroutine.gF$_lVhF$_CopyVariablesFromParent(program,
							freevars.asList())
					&& program.pPg$hO_BoxArguments(parameters.length)
					&& program.g_InvokeMethod(GenericFunction.class
							.getDeclaredMethod("run", Object[].class));
		} catch (SecurityException e) {
			log.error("Failed to get GenericFunction.run method.", e);
		} catch (NoSuchMethodException e) {
			log.error("Failed to get GenericFunction.run method.", e);
		}
		return false;
	}

	@Override
	public void resetType() {
		expression.resetType();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		AstNode resultexpression = expression.resolve(runner, context,
				environment);
		if (resultexpression == null) {
			return null;
		} else {
			return new AstFixedPoint2(self, resultexpression);
		}
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print('$');
		print.print(self.name);
		print.print(' ');
		print.print(expression);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return runner.typeCheck(expression, self.getType(), context);
	}
}
