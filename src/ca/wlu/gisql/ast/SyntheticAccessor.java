package ca.wlu.gisql.ast;

import java.util.Iterator;

import org.apache.commons.collections15.iterators.EmptyIterator;
import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.MaskedEnvironment;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * A variable which is really something else.
 */
abstract class SyntheticAccessor extends NamedVariable {

	protected final String name;

	protected final Type type;

	protected SyntheticAccessor(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	final ResolutionEnvironment createEnvironment(ResolutionEnvironment original) {
		return new MaskedEnvironment<SyntheticAccessor>(this, original);
	}

	@Override
	protected final void freeVariables(
			ListOrderedSet<VariableInformation> variables) {
	}

	public final Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public final Type getType() {
		return type;
	}

	@Override
	public final String getVariableName() {
		return name;
	}

	@Override
	public final Iterator<AstNode> iterator() {
		return EmptyIterator.getInstance();
	}

	@Override
	public final void resetType() {
		type.reset();
	}

	@Override
	public final AstNode resolve(ExpressionRunner runner,
			ExpressionContext context, ResolutionEnvironment environment) {
		return this;
	}

	public final void show(ShowablePrintWriter<AstNode> print) {
		print.print(name);
	}

	@Override
	public final boolean type(ExpressionRunner runner, ExpressionContext context) {
		return true;
	}

}