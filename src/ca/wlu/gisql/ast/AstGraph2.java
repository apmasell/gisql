package ca.wlu.gisql.ast;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import name.masella.iterator.ArrayIterator;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Renderable;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.RenderingGraphMatcher;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.graph.SubgraphMatcher;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Phase 2 representation of a graph “FLOWR” expression to select subgraphs from
 * an interactome.
 */
public class AstGraph2 extends AstNode {

	private final class ConnectionComparator implements
			Comparator<AstParameter> {
		private final int start;
		private final AstParameter[] variables;

		private ConnectionComparator(AstParameter[] variables, int start) {
			this.variables = variables;
			this.start = start;
		}

		@Override
		public int compare(AstParameter left, AstParameter right) {
			return score(right) - score(left);
		}

		private int score(AstParameter node) {
			if (start == 0) {
				return connections.degreeOf(node);
			}
			for (int index = 0; index < start; index++) {
				if (connections.getEdge(variables[index], node) != null) {
					return connections.degreeOf(node)
							- disconnections.degreeOf(node);

				}
			}
			return Integer.MIN_VALUE;
		}
	}

	private static final Logger log = Logger.getLogger(AstGraph2.class);

	private static final Renderable trueexpression = new Renderable() {

		@Override
		public <C> boolean render(Rendering<C> rendering, int depth) {
			return rendering.hO_AsObject(true);
		}
	};

	private final SimpleGraph<AstParameter, DefaultEdge> connections;

	private final SimpleGraph<AstParameter, DefaultEdge> disconnections;

	private final AstNode fromexpression;

	private final AstNode returnexpression;

	private final Type type;

	private final AstNode whereexpression;

	public AstGraph2(SimpleGraph<AstParameter, DefaultEdge> connections,
			SimpleGraph<AstParameter, DefaultEdge> disconnections,
			AstNode fromexpression, AstNode whereexpression,
			AstNode returnexpression) {
		this.connections = connections;
		this.disconnections = disconnections;
		this.whereexpression = whereexpression;
		this.fromexpression = fromexpression;
		this.returnexpression = returnexpression;
		type = new ListType(returnexpression.getType());
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		if (whereexpression != null) {
			whereexpression.freeVariables(variables);
		}
		returnexpression.freeVariables(variables);
		fromexpression.freeVariables(variables);
		for (AstParameter parameter : connections.vertexSet()) {
			variables.remove(parameter.variableInformation);
		}
	}

	@Override
	public ResolutionEnvironment getModifiedEnvironment(
			ResolutionEnvironment environment) {
		ResolutionEnvironment fromenvironment = fromexpression
				.getModifiedEnvironment(environment);
		return returnexpression
				.getModifiedEnvironment(whereexpression == null ? fromenvironment
						: whereexpression
								.getModifiedEnvironment(fromenvironment));
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
	public Iterator<AstNode> iterator() {
		return new ArrayIterator<AstNode>(fromexpression, whereexpression,
				returnexpression);
	}

	@Override
	protected <T> boolean renderSelf(Rendering<T> program, int depth) {
		/* Sort the witnesses so that the highest degree witness is first. */
		AstParameter[] variables = connections.vertexSet().toArray(
				new AstParameter[0]);
		for (int start = 0; start < variables.length - 1; start++) {
			Arrays.sort(variables, start, variables.length,
					new ConnectionComparator(variables, start));
		}

		/* Build index of variables. */
		String[] nodenames = new String[variables.length];
		Map<AstParameter, Integer> indicies = new HashMap<AstParameter, Integer>();
		for (int index = 0; index < variables.length; index++) {
			nodenames[index] = variables[index].getVariableName();
			indicies.put(variables[index], index);
		}

		StringBuffer sb = new StringBuffer();
		sb.append("In ").append(this).append(
				" variables have been assigned as follows: ").append(indicies);

		RenderingGraphMatcher subprogram = new RenderingGraphMatcher(nodenames,
				setupMatcher(connections, indicies, sb.append(" Connected:")),
				setupMatcher(disconnections, indicies, sb
						.append(" Disconnected:")), toString());

		log.debug(sb.toString());

		/* Pass along required variables.. */
		ListOrderedSet<VariableInformation> freevars = this.freeVariables();

		/* Create a matcher */

		try {
			return subprogram.gF$_CreateFields(freevars.asList(), program)
					&& subprogram
							.createWhereMethod(whereexpression == null ? trueexpression
									: whereexpression)
					&& subprogram.createReturnMethod(returnexpression)
					&& program.hO_CreateSubroutine(subprogram)
					&& subprogram.gF$_lVhF$_CopyVariablesFromParent(program,
							freevars.asList())
					&& program.lOhO()
					&& fromexpression.render(program, 0)
					&& program.g_InvokeMethod(SubgraphMatcher.class.getMethod(
							"match", Interactome.class));
		} catch (SecurityException e) {
			log.error("Failed to get method.", e);
		} catch (NoSuchMethodException e) {
			log.error("Failed to get method.", e);
		}
		return false;
	}

	@Override
	public void resetType() {
		type.reset();
		fromexpression.resetType();
		returnexpression.resetType();
		if (whereexpression != null) {
			whereexpression.resetType();
		}
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {

		AstNode resultfromexpression = fromexpression.resolve(runner, context,
				environment);
		AstNode resultwhereexpression = whereexpression == null ? null
				: whereexpression.resolve(runner, context, environment);
		AstNode resultreturnexpression = returnexpression.resolve(runner,
				context, environment);
		if (resultfromexpression == null || resultwhereexpression == null
				^ whereexpression == null || resultreturnexpression == null) {
			return null;
		} else {
			return new AstGraph2(connections, disconnections,
					resultfromexpression, resultwhereexpression,
					resultreturnexpression);
		}
	}

	private <T> List<Point> setupMatcher(
			SimpleGraph<AstParameter, DefaultEdge> graph,
			Map<AstParameter, Integer> indicies, StringBuffer sb) {

		List<Point> results = new ArrayList<Point>();

		for (DefaultEdge edge : graph.edgeSet()) {
			int source = indicies.get(graph.getEdgeSource(edge));
			int target = indicies.get(graph.getEdgeTarget(edge));

			sb.append(" (").append(source).append(", ").append(target).append(
					')');
			results.add(new Point(source, target));
		}
		return results;
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("for ");
		if (connections.vertexSet().size() == 1) {
			for (AstParameter parameter : connections.vertexSet()) {
				print.print(parameter);
			}
		} else {

			showGraph(disconnections, print, showGraph(connections, print,
					true, true), false);
		}
		print.print(" in ");
		print.print(fromexpression);
		if (whereexpression != null) {
			print.print(" where ");
			print.print(whereexpression);
		}

		print.print(" return ");
		print.print(returnexpression);
	}

	private boolean showGraph(SimpleGraph<AstParameter, DefaultEdge> graph,
			ShowablePrintWriter<AstNode> print, boolean first, boolean present) {
		for (DefaultEdge edge : graph.edgeSet()) {
			if (first) {
				first = false;
			} else {
				print.print(", ");
			}
			print.print(graph.getEdgeSource(edge));
			print.print('(');
			if (!present) {
				print.print('¬');
			}
			print.print(graph.getEdgeTarget(edge));
			print.print(')');
		}
		return first;
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return returnexpression.type(runner, context)
				&& runner.typeCheck(fromexpression, Type.InteractomeType,
						context)
				&& (whereexpression == null ? true : runner.typeCheck(
						whereexpression, Type.BooleanType, context));
	}

}
