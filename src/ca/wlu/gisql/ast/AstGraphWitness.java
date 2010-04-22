package ca.wlu.gisql.ast;

import java.util.Set;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.NamedVariable;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** Reference to a gene in a graph when searching for subgraphs. */
class AstGraphWitness extends AstNode implements NamedVariable {

	private final String name;

	AstGraphWitness(String variable) {
		name = variable;
	}

	@Override
	protected void freeVariables(Set<String> variables) {
		variables.add(name);
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return Type.GeneType;
	}

	@Override
	public String getVariableName() {
		return name;
	}

	@Override
	protected boolean renderSelf(Rendering program, int depth) {
		return program.lRhO(name);
	}

	@Override
	public void resetType() {
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		return this;
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(name);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return true;
	}

}
