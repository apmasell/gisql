package ca.wlu.gisql.ast;

import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.MaybeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.RenderingInteractome;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstInteractome2 extends AstNode {

	private final AstParameter gene;
	private final AstParameter gene1;
	private final AstParameter gene2;
	private final AstNode geneexpression;
	private final AstNode interactionexpression;
	private final AstNode membership;
	private final Type type;
	private final AstParameter[] unknownvariables;
	private final AstParameter[] variables;

	public AstInteractome2(AstParameter[] variables,
			AstParameter[] unknownvariables, AstParameter gene,
			AstParameter gene1, AstParameter gene2, AstNode membership,
			AstNode geneexpression, AstNode interactionexpression) {
		this.unknownvariables = unknownvariables.clone();
		this.variables = variables.clone();
		this.gene = gene;
		this.gene1 = gene1;
		this.gene2 = gene2;
		this.membership = membership;
		this.geneexpression = geneexpression;
		this.interactionexpression = interactionexpression;
		Type type = Type.InteractomeType;
		for (int index = 0; index < variables.length; index++) {
			type = new ArrowType(Type.InteractomeType, type);
		}
		this.type = type;
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		membership.freeVariables(variables);
		geneexpression.freeVariables(variables);
		interactionexpression.freeVariables(variables);
		for (AstParameter variable : this.variables) {
			variables.remove(variable.variableInformation);
		}
		for (AstParameter variable : unknownvariables) {
			variables.remove(variable.variableInformation);
		}
		variables.remove(gene.variableInformation);
		variables.remove(gene1.variableInformation);
		variables.remove(gene2.variableInformation);
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	protected <C> boolean renderSelf(Rendering<C> program, int depth) {
		String[] variablenames = new String[variables.length];
		for (int index = 0; index < variablenames.length; index++) {
			variablenames[index] = variables[index].name;
		}

		RenderingInteractome subprogram = new RenderingInteractome(membership,
				toString(), variablenames);
		ListOrderedSet<VariableInformation> freevariables = freeVariables();
		return subprogram.gF$_CreateFields(freevariables.asList())
				&& subprogram.createGeneMethod(gene, geneexpression)
				&& subprogram.createInteractomeMethod(gene1, gene2,
						interactionexpression)
				&& program.hO_CreateSubroutine(subprogram)
				&& subprogram.gF$_lVhF$_CopyVariablesFromParent(program,
						freevariables.asList());
	}

	@Override
	public void resetType() {
		geneexpression.resetType();
		interactionexpression.resetType();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		AstNode resultgeneexpression = geneexpression.resolve(runner, context,
				environment);
		AstNode resultinteractionexpression = interactionexpression.resolve(
				runner, context, environment);
		AstNode resultmembership = membership.resolve(runner, context,
				environment);
		if (resultgeneexpression == null || resultinteractionexpression == null
				|| resultmembership == null) {
			return null;
		} else if (resultgeneexpression == geneexpression
				&& resultinteractionexpression == interactionexpression
				&& resultmembership == membership) {
			return this;
		} else {
			return new AstInteractome2(variables, unknownvariables, gene,
					gene1, gene2, membership, resultgeneexpression,
					resultinteractionexpression);
		}
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("interactome");
		if (variables.length > 0) {
			print.print(" given ");
			boolean first = true;
			for (AstParameter variable : variables) {
				if (first) {
					first = false;
				} else {
					print.print(", ");
				}
				print.print(variable);
			}
		}
		print.print(" { unknown = ");
		print.print(membership);
		print.print("; gene ");
		print.print(gene);
		print.print(" = ");
		print.print(geneexpression);
		print.print("; expression ");
		print.print(gene1);
		print.print(' ');
		print.print(gene2);
		print.print(" = ");
		print.print(interactionexpression);
		print.print('}');
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		if (!(geneexpression.type(runner, context)
				&& interactionexpression.type(runner, context) && membership
				.type(runner, context))) {
			return false;
		}
		if (!membership.getType().unify(Type.MembershipType)) {
			runner.appendTypeError(membership.getType(), Type.MembershipType,
					membership, context);
			return false;
		}
		return typeExpression(geneexpression, runner, context)
				&& typeExpression(interactionexpression, runner, context);
	}

	private boolean typeExpression(AstNode expression, ExpressionRunner runner,
			ExpressionContext context) {
		if (expression.getType().canUnify(Type.MembershipType)) {
			if (!expression.getType().unify(Type.MembershipType)) {
				runner.appendTypeError(expression.getType(),
						Type.MembershipType, expression, context);
				return false;
			} else {
				return true;
			}
		}

		if (expression.getType().canUnify(new MaybeType(Type.MembershipType))) {
			if (!expression.getType().unify(new MaybeType(Type.MembershipType))) {
				runner.appendTypeError(expression.getType(), new MaybeType(
						Type.MembershipType), expression, context);
				return false;
			} else {
				return true;
			}
		}

		runner.appendTypeError(expression.getType(), Type.MembershipType,
				expression, context);
		return false;

	}

}
