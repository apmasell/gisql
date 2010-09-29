package ca.wlu.gisql.ast;

import java.util.Iterator;

import org.apache.commons.collections15.iterators.EmptyIterator;
import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.MaskedEnvironment;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.descriptors.ast.AssignmentDescriptor;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Store a copy of a value as a variable in the {@link UserEnvironment}.
 */
public class AstEnvironmentStore extends AstNode {
	private final Type currenttype;

	private final String name;

	private AstNode node;

	public AstEnvironmentStore(AstNode node, String name, Type currenttype) {
		this.node = node;
		this.name = name;
		this.currenttype = currenttype;
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		node.freeVariables(variables);
	}

	@Override
	public ResolutionEnvironment getModifiedEnvironment(
			ResolutionEnvironment environment) {
		return new MaskedEnvironment<NamedVariable>(new AstEnvironmentLoad(
				name, node.getType()), node.getModifiedEnvironment(environment));
	}

	public Precedence getPrecedence() {
		return AssignmentDescriptor.descriptor.getPrecedence();
	}

	@Override
	public Type getType() {
		return Type.UnitType;
	}

	@Override
	public Iterator<AstNode> iterator() {
		return EmptyIterator.getInstance();
	}

	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		return node.render(program, depth)
				&& program.pOsE(name, node.getType()) && program.lFhO_Nil();
	}

	@Override
	public void resetType() {
		node.resetType();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		AstNode node = this.node.resolve(runner, context, environment);
		if (node == null) {
			return null;
		} else {
			this.node = node;
			return this;
		}
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(name);
		print.print(" = ");
		print.print(node);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		if (!node.type(runner, context)) {
			return false;
		}
		if (currenttype == null) {
			return true;
		}
		if (node.getType().toString().equals(currenttype.toString())) {
			return true;
		} else {
			runner.appendTypeError(node.getType(), currenttype, this, context);
			return false;
		}
	}
}