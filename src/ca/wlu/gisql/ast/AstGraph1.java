package ca.wlu.gisql.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.MaskedEnvironment;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.graph.MaybeEdge;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Phase 1 representation of a graph “FLOWR” expression to select subgraphs from
 * an interactome.
 */
public class AstGraph1 extends AstNode {

	private final AstNode fromexpression;

	private final SimpleGraph<String, MaybeEdge> graph;

	private final AstNode returnexpression;

	private final AstNode whereexpression;

	public AstGraph1(SimpleGraph<String, MaybeEdge> graph,
			AstNode fromexpression, AstNode whereexpression,
			AstNode returnexpression) {
		this.graph = graph;
		this.fromexpression = fromexpression;
		this.whereexpression = whereexpression;
		this.returnexpression = returnexpression;
	}

	@Override
	protected void freeVariables(Set<String> variables) {
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
	protected <T> boolean renderSelf(Rendering<T> program, int depth) {
		return raiseIllegalState();
	}

	@Override
	public void resetType() {
		raiseIllegalState();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {

		/*
		 * Create graphs of present and absent edges. Also, create an new
		 * environment with the graph parameters defined.
		 */
		SimpleGraph<AstGraphWitness, DefaultEdge> connections = new SimpleGraph<AstGraphWitness, DefaultEdge>(
				DefaultEdge.class);
		SimpleGraph<AstGraphWitness, DefaultEdge> disconnections = new SimpleGraph<AstGraphWitness, DefaultEdge>(
				DefaultEdge.class);
		ResolutionEnvironment boundenvironment = environment;

		Map<String, AstGraphWitness> map = new HashMap<String, AstGraphWitness>();
		for (String variable : graph.vertexSet()) {
			AstGraphWitness witness = new AstGraphWitness(variable);
			boundenvironment = new MaskedEnvironment<AstGraphWitness>(witness,
					boundenvironment);
			connections.addVertex(witness);
			disconnections.addVertex(witness);
			map.put(variable, witness);
		}

		for (MaybeEdge edge : graph.edgeSet()) {
			(edge.isPresent() ? connections : disconnections).addEdge(map
					.get(graph.getEdgeSource(edge)), map.get(graph
					.getEdgeTarget(edge)));
		}

		AstNode resultfromexpression = fromexpression.resolve(runner, context,
				environment);
		AstNode resultwhereexpression = whereexpression == null ? null
				: whereexpression.resolve(runner, context, boundenvironment);
		AstNode resultreturnexpression = returnexpression.resolve(runner,
				context, boundenvironment);

		if (resultfromexpression == null || resultwhereexpression == null
				^ whereexpression == null || resultreturnexpression == null) {
			return null;
		}

		return new AstGraph2(connections, disconnections, resultfromexpression,
				resultwhereexpression, resultreturnexpression);
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("for ");
		boolean first = true;
		for (MaybeEdge edge : graph.edgeSet()) {
			if (first) {
				first = false;
			} else {
				print.print(", ");
			}
			print.print(graph.getEdgeSource(edge));
			print.print('(');
			if (!edge.isPresent()) {
				print.print('¬');
			}
			print.print(graph.getEdgeTarget(edge));
			print.print(')');
		}
		print.print(' ');
		if (whereexpression != null) {
			print.print("where ");
			print.print(whereexpression);
		}

		print.print(" return ");
		print.print(returnexpression);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return raiseIllegalState();
	}

}
