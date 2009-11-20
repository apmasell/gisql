package ca.wlu.gisql.ast;

import java.util.Set;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * The variable represented by a lambda expression. (i.e., the <b>x</b> in
 * <tt>(λ x. f <b>x</b> y)</tt>.)
 */
public class AstLambdaParameter extends AstNode {

	final String name;

	final TypeVariable type = new TypeVariable();

	public AstLambdaParameter(String name) {
		this.name = name;
	}

	@Override
	protected void freeVariables(Set<String> variables) {
		variables.add(name);
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return type;
	}

	/**
	 * Adds an instruction to copy a variable from the variable on the operand
	 * stack.
	 */
	@Override
	public boolean renderSelf(Rendering program, int depth) {
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