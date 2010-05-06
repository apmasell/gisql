package ca.wlu.gisql.ast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.RenderingFunction;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.graph.Gene;
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
			Comparator<AstGraphWitness> {
		private final int start;
		private final AstGraphWitness[] variables;

		private ConnectionComparator(AstGraphWitness[] variables, int start) {
			this.variables = variables;
			this.start = start;
		}

		@Override
		public int compare(AstGraphWitness left, AstGraphWitness right) {
			return score(right) - score(left);
		}

		private int score(AstGraphWitness node) {
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

	private final SimpleGraph<AstGraphWitness, DefaultEdge> connections;

	private final SimpleGraph<AstGraphWitness, DefaultEdge> disconnections;

	private final AstNode fromexpression;

	private final AstNode returnexpression;

	private final Type type;

	private final AstNode whereexpression;

	public AstGraph2(SimpleGraph<AstGraphWitness, DefaultEdge> connections,
			SimpleGraph<AstGraphWitness, DefaultEdge> disconnections,
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
	protected void freeVariables(Set<String> variables) {
		Set<String> toberemoved = new HashSet<String>();
		for (AstGraphWitness variable : connections.vertexSet()) {
			if (!variables.contains(variable.getVariableName())) {
				toberemoved.add(variable.getVariableName());
			}
		}
		returnexpression.freeVariables(variables);
		variables.removeAll(toberemoved);
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
	protected <T> boolean renderSelf(Rendering<T> program, int depth) {
		try {
			String resultlist = "$" + Integer.toHexString(hashCode());
			/* Create a subroutine that takes the witnesses as arguments. */
			Type[] arguments = new Type[connections.vertexSet().size()];
			Arrays.fill(arguments, Type.GeneType);
			Rendering<GenericFunction> subroutine = new RenderingFunction(
					toString(), Type.UnitType, arguments);

			/* This will be our resultant list. */
			if (!(program.pRg$hO_CreateObject(ArrayList.class.getConstructor()) && program
					.hR_CreateLocal(resultlist, List.class))) {
				return false;
			}

			Set<String> freevars = this.freeVariables();
			freevars.add(resultlist);
			if (!subroutine.gF$_CreateFields(freevars)) {
				return false;
			}

			/* Sort the witnesses so that the highest degree witness is first. */
			final AstGraphWitness[] variables = connections.vertexSet()
					.toArray(new AstGraphWitness[0]);
			for (int start = 0; start < variables.length - 1; start++) {
				Arrays.sort(variables, start, variables.length,
						new ConnectionComparator(variables, start));
			}

			/* Relocate the anonymous arguments into named local variables. */
			Map<AstGraphWitness, Integer> indicies = new HashMap<AstGraphWitness, Integer>();
			for (int index = variables.length - 1; index >= 0; index--) {
				if (!(subroutine.pPg() && subroutine.hR_CreateLocal(
						variables[index].getVariableName(), Gene.class))) {
					return false;
				}
				indicies.put(variables[index], index);
			}

			StringBuffer sb = new StringBuffer();
			sb.append("In ").append(this).append(
					" variables have been assigned as follows: ").append(
					indicies);
			/* Create a matcher */
			if (!(program.hP(variables.length)
					&& program.hP(new Rendering.Cast(fromexpression,
							Interactome.class))
					&& program.pRg$hO_CreateObject(SubgraphMatcher.class
							.getConstructors()[0])
					&& setupMatcher(program, connections, indicies,
							SubgraphMatcher.class.getMethod("connect",
									int.class, int.class), sb
									.append(" Connected:")) && setupMatcher(
					program, disconnections, indicies, SubgraphMatcher.class
							.getMethod("disconnect", int.class, int.class), sb
							.append(" Disconnected:")))) {
				return false;
			}
			log.debug(sb.toString());

			Label skip = new Label();
			return (whereexpression == null ? true : whereexpression.render(
					subroutine, depth + arguments.length)
					&& subroutine.pOhO_ObjectToPrimitive(Boolean.class)
					&& subroutine.jump(Opcodes.IFEQ, skip))
					&& subroutine.lRhO(resultlist)
					&& returnexpression.render(subroutine, depth
							+ arguments.length)
					&& subroutine.g_InvokeMethod(List.class.getMethod("add",
							Object.class))
					&& subroutine.mark(skip)
					&& subroutine.hO_AsObject(0)
					&& program.hO_CreateSubroutine(subroutine)
					&& subroutine.gF$_lVhF$_CopyVariablesFromParent(program,
							freevars)
					&& program.g_InvokeMethod(SubgraphMatcher.class.getMethod(
							"match", GenericFunction.class))
					&& program.lRhO(resultlist);
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

	private <T> boolean setupMatcher(Rendering<T> program,
			SimpleGraph<AstGraphWitness, DefaultEdge> graph,
			Map<AstGraphWitness, Integer> indicies, Method method,
			StringBuffer sb) {
		for (DefaultEdge edge : graph.edgeSet()) {
			int source = indicies.get(graph.getEdgeSource(edge));
			int target = indicies.get(graph.getEdgeTarget(edge));

			sb.append(" (").append(source).append(", ").append(target).append(
					')');

			if (!(program.lOhO() && program.hO(source) && program.hO(target) && program
					.g_InvokeMethod(method))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("for ");
		showGraph(disconnections, print, showGraph(connections, print, true,
				true), false);

		print.print(' ');
		if (whereexpression != null) {
			print.print("where ");
			print.print(whereexpression);
		}

		print.print(" return ");
		print.print(returnexpression);
	}

	private boolean showGraph(SimpleGraph<AstGraphWitness, DefaultEdge> graph,
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
		if (!fromexpression.type(runner, context) || whereexpression != null
				&& !whereexpression.type(runner, context)
				|| !returnexpression.type(runner, context)) {
			return false;
		}
		if (!fromexpression.getType().unify(Type.InteractomeType)) {
			runner.appendTypeError(fromexpression.getType(),
					Type.InteractomeType, this, context);
			return false;
		}
		if (whereexpression != null
				&& !whereexpression.getType().unify(Type.BooleanType)) {
			runner.appendTypeError(whereexpression.getType(), Type.BooleanType,
					this, context);
			return false;
		}
		return true;
	}

}
