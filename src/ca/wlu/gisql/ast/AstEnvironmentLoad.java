package ca.wlu.gisql.ast;

import java.util.Iterator;

import org.apache.commons.collections15.iterators.EmptyIterator;
import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * The {@link AstNode} of an known symbol in the environment. This will fetch a
 * symbol during execution.
 */
public class AstEnvironmentLoad extends NamedVariable {

	private final String name;
	private final Type shadowtype = new TypeVariable();
	private final Type type;

	public AstEnvironmentLoad(Function function) {
		this(function.toString(), function.getType());
	}

	public AstEnvironmentLoad(String name, Type type) {
		super();
		this.name = name;
		this.type = type;
	}

	@Override
	ResolutionEnvironment createEnvironment(ResolutionEnvironment environment) {
		return environment;
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		/*
		 * This variable is considered bound in the environment, even though, by
		 * classical reasoning, this would be considered a free variable.
		 */
	}

	@Override
	public ResolutionEnvironment getModifiedEnvironment(
			ResolutionEnvironment environment) {
		return environment;
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return shadowtype;
	}

	@Override
	public String getVariableName() {
		return name;
	}

	@Override
	public Iterator<AstNode> iterator() {
		return EmptyIterator.getInstance();
	}

	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		return program.lEhO(name)
				&& program.g_Cast(type.getRootJavaType())
				&& (depth > 0 && shadowtype.getArrowDepth() == depth ? AstNativeGenericFunction
						.renderSelf(shadowtype, program, depth)
						: true);
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

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(name);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return shadowtype.unify(type.fresh());
	}
}
