package ca.wlu.gisql.ast;

import java.util.Iterator;

import org.apache.commons.collections15.iterators.SingletonIterator;
import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.MaskedEnvironment;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Phase 1 representation of a lambda expression. There will be cleaned during
 * resolution and replaced with phase 2 lambda expressions.
 */
public class AstLambda1 extends AstNode {
	private final AstNode expression;

	final String name;

	private final Type type;

	public AstLambda1(String variable, AstNode expression, Type type) {
		name = variable;
		this.expression = expression;
		this.type = type;
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		raiseIllegalState();
	}

	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public Iterator<AstNode> iterator() {
		return new SingletonIterator<AstNode>(expression);
	}

	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		return raiseIllegalState();
	}

	@Override
	public void resetType() {
		raiseIllegalState();
	}

	/**
	 * Set up a fake environment where the variable name is mapped to a
	 * {@link AstParameter}. Then encapsulate the result in a phase 2 lambda.
	 */
	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		MaskedEnvironment<AstParameter> maskedenvironment = new MaskedEnvironment<AstParameter>(
				new AstParameter(name, type), environment);
		AstNode resultexpression = expression.resolve(runner, context,
				maskedenvironment);
		if (resultexpression == null) {
			return null;
		} else {
			return new AstLambda2(maskedenvironment.getVariable(),
					resultexpression);
		}
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("\\");
		print.print(name);
		if (type != null) {
			print.print(" :: ");
			print.print(type);
		}
		print.print(" -> ");
		print.print(expression);
		print.print(')');
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return raiseIllegalState();
	}
}
