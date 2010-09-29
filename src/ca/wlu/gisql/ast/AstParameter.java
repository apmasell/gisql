package ca.wlu.gisql.ast;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.iterators.EmptyIterator;
import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.MaskedEnvironment;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * The variable represented by a lambda expression (i.e., the <b>x</b> in
 * <tt>(λ x. f <b>x</b> y)</tt>.), or the witness in a graph.
 */
public class AstParameter extends NamedVariable {

	final String name;

	private final Set<NamedVariable> subordinates;

	final Type type;

	final VariableInformation variableInformation;

	public AstParameter(String name) {
		this(name, new TypeVariable());
	}

	public AstParameter(String name, Type type) {
		this(name, type, null);
	}

	public AstParameter(String name, Type type, Set<NamedVariable> subordinates) {
		this.name = name;
		this.type = type;
		this.subordinates = subordinates;
		variableInformation = new VariableInformation(name, type);
	}

	@Override
	ResolutionEnvironment createEnvironment(ResolutionEnvironment original) {
		ResolutionEnvironment environment = new MaskedEnvironment<AstParameter>(
				this, original);
		if (subordinates != null) {
			for (NamedVariable parameter : subordinates) {
				environment = parameter.createEnvironment(environment);
			}
		}
		return environment;
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		variables.add(variableInformation);
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
		return type;
	}

	@Override
	public String getVariableName() {
		return name;
	}

	@Override
	public Iterator<AstNode> iterator() {
		return EmptyIterator.getInstance();
	}

	/**
	 * Adds an instruction to copy a variable from the variable on the operand
	 * stack.
	 */
	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		return program.lRhO(name);
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
		return true;
	}

}