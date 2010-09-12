package ca.wlu.gisql.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.lang.model.type.TypeVariable;

import org.apache.commons.collections15.iterators.SingletonIterator;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

/**
 * Special node that returns the type of its argument rather than the value of
 * the argument.
 */
public class AstTypeOf extends AstNode {
	private static final Logger log = Logger.getLogger(AstTypeOf.class);

	private final AstNode parameter;

	public AstTypeOf(AstNode parameter) {
		super();
		this.parameter = parameter;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AstTypeOf) {
			return parameter.equals(((AstTypeOf) other).parameter);
		} else {
			return false;
		}
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		parameter.freeVariables(variables);
	}

	public Precedence getPrecedence() {
		return Precedence.Assignment;
	}

	@Override
	public Type getType() {
		return Type.TypeType;
	}

	@Override
	public int hashCode() {
		return parameter.hashCode() * 227;
	}

	@Override
	public Iterator<AstNode> iterator() {
		return new SingletonIterator<AstNode>(parameter);
	}

	private void printTree(ShowablePrintWriter<List<TypeVariable>> print,
			AstNode node, int depth) {
		for (int count = 0; count < depth; count++) {
			print.print('\t');
		}
		print.print(node);
		print.print(" :: ");
		print.print(node.getType());
		print.println();
		for (AstNode child : node) {
			printTree(print, child, depth + 1);
		}
	}

	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		if (GisQL.debug) {
			ShowablePrintWriter<List<TypeVariable>> print = new ShowableStringBuilder<List<TypeVariable>>(
					new ArrayList<TypeVariable>());
			printTree(print, parameter, 0);
			log.debug(print);
		}
		return parameter.getType().render(program, 0);
	}

	@Override
	public void resetType() {
		parameter.resetType();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		AstNode parameter = this.parameter
				.resolve(runner, context, environment);
		if (parameter == null) {
			return null;
		} else {
			return new AstTypeOf(parameter);
		}
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("typeof ");
		print.print(parameter);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return parameter.type(runner, context);
	}
}
