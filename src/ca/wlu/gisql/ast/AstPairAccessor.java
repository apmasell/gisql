package ca.wlu.gisql.ast;

import java.util.Iterator;

import org.apache.commons.collections15.iterators.EmptyIterator;
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
 * A variable which is really an index into a tuple.
 */
public class AstPairAccessor extends NamedVariable {

	final String name;

	private final String parent;

	private final boolean[] selectors;

	final Type type;

	public AstPairAccessor(String parent, String name, Type type,
			boolean... selectors) {
		this.parent = parent;
		this.name = name;
		this.type = type;
		this.selectors = selectors;
	}

	@Override
	ResolutionEnvironment createEnvironment(ResolutionEnvironment original) {
		return new MaskedEnvironment<AstPairAccessor>(this, original);
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
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
		return program.lRhO_PairAccess(parent, type.getRootJavaType(),
				selectors);
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