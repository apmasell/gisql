package ca.wlu.gisql.ast;

import java.util.Iterator;

import org.apache.commons.collections15.iterators.SingletonIterator;
import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.ParameterDeclaration;
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
	final ParameterDeclaration declaration;

	private final AstNode expression;

	public AstLambda1(ParameterDeclaration declaration, AstNode expression) {
		this.declaration = declaration;
		this.expression = expression;
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		raiseIllegalState();
	}

	@Override
	public ResolutionEnvironment getModifiedEnvironment(
			ResolutionEnvironment environment) {
		return raiseIllegalState();
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
		AstParameter parameter = declaration.synthesize();
		AstNode resultexpression = expression.resolve(runner, context,
				parameter.createEnvironment(environment));
		if (resultexpression == null) {
			return null;
		} else {
			return new AstLambda2(parameter, resultexpression);
		}
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("\\");
		print.print(declaration);
		if (declaration.hasType()) {
			print.print(" :: ");
			print.print(declaration.hasType());
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
