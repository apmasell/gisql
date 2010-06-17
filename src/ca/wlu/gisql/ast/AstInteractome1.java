package ca.wlu.gisql.ast;

import java.util.Iterator;

import name.masella.iterator.ArrayIterator;

import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.MaybeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.MaskedEnvironment;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstInteractome1 extends AstNode {

	private final String gene1name;
	private final String gene2name;
	private final AstNode geneexpression;
	private final String genename;
	private final AstNode interactionexpression;
	private final AstNode membership;
	private final String[] variables;

	public AstInteractome1(String[] variables, String genename,
			String gene1name, String gene2name, AstNode membership,
			AstNode geneexpression, AstNode interactionexpression) {
		this.variables = variables.clone();
		this.genename = genename;
		this.gene1name = gene1name;
		this.gene2name = gene2name;
		this.membership = membership;
		this.geneexpression = geneexpression;
		this.interactionexpression = interactionexpression;

	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		raiseIllegalState();
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	@Override
	public Type getType() {
		return raiseIllegalState();
	}

	@Override
	public Iterator<AstNode> iterator() {
		return new ArrayIterator<AstNode>(membership, geneexpression,
				interactionexpression);
	}

	@Override
	protected <C> boolean renderSelf(Rendering<C> program, int depth) {
		return raiseIllegalState();
	}

	@Override
	public void resetType() {
		raiseIllegalState();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {

		ResolutionEnvironment boundenvironment = environment;
		ResolutionEnvironment unknownboundenvironment = environment;

		AstParameter[] parameters = new AstParameter[variables.length];
		AstParameter[] unknownparameters = new AstParameter[variables.length];
		for (int index = 0; index < variables.length; index++) {
			AstParameter witness = new AstParameter(variables[index],
					new MaybeType(Type.MembershipType));
			parameters[index] = witness;
			boundenvironment = new MaskedEnvironment<AstParameter>(witness,
					boundenvironment);

			AstParameter unknownwitness = new AstParameter(variables[index],
					Type.MembershipType);
			unknownparameters[index] = unknownwitness;
			unknownboundenvironment = new MaskedEnvironment<AstParameter>(
					unknownwitness, unknownboundenvironment);
		}

		AstParameter gene = new AstParameter(genename, Type.GeneType);
		AstParameter gene1 = new AstParameter(gene1name, Type.GeneType);
		AstParameter gene2 = new AstParameter(gene2name, Type.GeneType);
		AstNode resultgeneexpression = geneexpression.resolve(runner, context,
				new MaskedEnvironment<AstParameter>(gene, boundenvironment));
		AstNode resultinteractionexpression = interactionexpression.resolve(
				runner, context, new MaskedEnvironment<AstParameter>(gene1,
						new MaskedEnvironment<AstParameter>(gene2,
								boundenvironment)));
		AstNode resultmembership = membership.resolve(runner, context,
				unknownboundenvironment);
		if (resultgeneexpression == null || resultinteractionexpression == null
				|| resultmembership == null) {
			return null;
		} else {
			return new AstInteractome2(parameters, unknownparameters, gene,
					gene1, gene2, resultmembership, resultgeneexpression,
					resultinteractionexpression);
		}
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("interactome");
		if (variables.length > 0) {
			print.print(" given ");
			boolean first = true;
			for (String variable : variables) {
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
		print.print(genename);
		print.print(" = ");
		print.print(geneexpression);
		print.print("; expression ");
		print.print(gene1name);
		print.print(' ');
		print.print(gene2name);
		print.print(" = ");
		print.print(interactionexpression);
		print.print('}');
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return raiseIllegalState();
	}
}
