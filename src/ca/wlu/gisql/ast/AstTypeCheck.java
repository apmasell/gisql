package ca.wlu.gisql.ast;

import java.util.Iterator;

import org.apache.commons.collections15.iterators.SingletonIterator;
import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstTypeCheck extends AstNode {

	private final AstNode parameter;
	private final Type type;

	public AstTypeCheck(AstNode parameter, Type type) {
		this.parameter = parameter;
		this.type = type;
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		parameter.freeVariables(variables);
	}

	@Override
	public ResolutionEnvironment getModifiedEnvironment(
			ResolutionEnvironment environment) {
		return parameter.getModifiedEnvironment(environment);
	}

	@Override
	public Precedence getPrecedence() {
		return parameter.getPrecedence();
	}

	@Override
	public Type getType() {
		return parameter.getType();
	}

	@Override
	public Iterator<AstNode> iterator() {
		return new SingletonIterator<AstNode>(parameter);
	}

	@Override
	protected <C> boolean renderSelf(Rendering<C> program, int depth) {
		return parameter.render(program, depth);
	}

	@Override
	public void resetType() {
		type.reset();
		parameter.resetType();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		AstNode resultparameter = parameter.resolve(runner, context,
				environment);
		if (resultparameter == parameter) {
			return this;
		} else {
			return resultparameter == null ? null : new AstTypeCheck(
					resultparameter, type);
		}
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(parameter, getPrecedence());
		print.print(" :: ");
		print.print(type);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return runner.typeCheck(parameter, type, context);
	}

}
