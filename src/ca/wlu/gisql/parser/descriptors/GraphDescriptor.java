package ca.wlu.gisql.parser.descriptors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import name.masella.iterator.NestedIterator;

import org.apache.commons.collections15.iterators.SingletonIterator;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UndirectedSubgraph;

import ca.wlu.gisql.ast.AstGraph1;
import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.graph.MaybeEdge;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionChild;
import ca.wlu.gisql.parser.TokenHasCharacter;
import ca.wlu.gisql.parser.TokenMaybe;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.parser.TokenReservedWord;
import ca.wlu.gisql.parser.TokenSequence;
import ca.wlu.gisql.parser.TokenTree;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/** Syntax for a FLWOR expression. */
public class GraphDescriptor extends Parseable {

	private class CompleteGraph extends GraphNode {

		private final SimpleGraph<String, MaybeEdge> graph;
		private final Set<GraphNode> neighbours = new HashSet<GraphNode>();

		public CompleteGraph(SimpleGraph<String, MaybeEdge> graph) {
			this.graph = graph;
		}

		@Override
		boolean addChild(GraphNode child, boolean present,
				ExpressionContext context, Stack<ExpressionError> error) {
			for (GraphNode neighbour : neighbours) {
				for (String name : neighbour) {
					for (String self : child) {
						if (!addEdge(graph, name, self, present, context, error)) {
							return false;
						}
					}
				}
			}
			neighbours.add(child);
			return true;
		}

		@Override
		boolean finish(ExpressionContext context, Stack<ExpressionError> error) {
			if (neighbours.isEmpty()) {
				error.push(new ExpressionError(context,
						"Complete graph has no neighbours", null));
				return false;
			} else {
				return true;
			}
		}

		public Iterator<String> iterator() {
			return new NestedIterator<String, GraphNode>(neighbours.iterator());
		}
	}

	private class CycleGraph extends GraphNode {
		private final SimpleGraph<String, MaybeEdge> graph;
		private final List<GraphNode> nodes = new ArrayList<GraphNode>();

		public CycleGraph(SimpleGraph<String, MaybeEdge> graph) {
			this.graph = graph;
		}

		@Override
		boolean addChild(GraphNode child, boolean present,
				ExpressionContext context, Stack<ExpressionError> error) {
			if (!present) {
				error.push(new ExpressionError(context,
						"Nodes in a cycle cannot be absent.", null));
				return false;
			}
			if (!nodes.isEmpty()) {
				for (String childnames : child) {
					for (String ancestor : nodes.get(nodes.size() - 1)) {
						if (!addEdge(graph, childnames, ancestor, true,
								context, error)) {
							return false;
						}
					}
				}
			}
			nodes.add(child);
			return true;
		}

		@Override
		boolean finish(ExpressionContext context, Stack<ExpressionError> error) {
			if (nodes.size() < 2) {
				error.push(new ExpressionError(context,
						"Cycle has less than two nodes.", null));
				return false;
			} else {
				for (String childnames : nodes.get(0)) {
					for (String ancestor : nodes.get(nodes.size() - 1)) {
						if (!addEdge(graph, childnames, ancestor, true,
								context, error)) {
							return false;
						}
					}
				}
			}
			return true;
		}

		@Override
		public Iterator<String> iterator() {
			return new NestedIterator<String, GraphNode>(nodes.iterator());
		}

	}

	private abstract class GraphNode implements Iterable<String> {
		abstract boolean addChild(GraphNode child, boolean present,
				ExpressionContext context, Stack<ExpressionError> error);

		protected boolean addEdge(SimpleGraph<String, MaybeEdge> graph,
				String child, String parent, boolean present,
				ExpressionContext context, Stack<ExpressionError> error) {
			MaybeEdge edge = graph.getEdge(child, parent);
			if (edge == null) {
				graph.addEdge(child, parent, new MaybeEdge(present));
			} else {
				if (edge.isPresent() != present) {
					error.push(new ExpressionError(context, "Edge between "
							+ child + " and " + parent + " is in conflict.",
							null));
					return false;
				}
			}
			return true;
		}

		abstract boolean finish(ExpressionContext context,
				Stack<ExpressionError> error);
	}

	private class ParentNode extends GraphNode {
		private final SimpleGraph<String, MaybeEdge> graph;
		private final String name;

		public ParentNode(String name, SimpleGraph<String, MaybeEdge> graph) {
			super();
			this.name = name;
			this.graph = graph;
			if (!graph.containsVertex(name)) {
				graph.addVertex(name);
			}

		}

		@Override
		boolean addChild(GraphNode child, boolean present,
				ExpressionContext context, Stack<ExpressionError> error) {
			for (String childnode : child) {
				if (!addEdge(graph, childnode, name, present, context, error)) {
					return false;
				}
			}
			return true;
		}

		@Override
		boolean finish(ExpressionContext context, Stack<ExpressionError> error) {
			return true;
		}

		@Override
		public Iterator<String> iterator() {
			return new SingletonIterator<String>(name);
		}

	}

	public final static Parseable descriptor = new GraphDescriptor();

	private GraphDescriptor() {
		super(new TokenReservedWord("for"), new TokenTree(',', '(', ')',
				new TokenSequence(new TokenHasCharacter('!', '¬'),
						TokenName.self)), new TokenReservedWord("in"),
				TokenExpressionChild.self, new TokenMaybe(new TokenSequence(
						new TokenReservedWord("where"),
						TokenExpressionChild.self)), new TokenReservedWord(
						"return"), TokenExpressionChild.self);
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		SimpleGraph<String, MaybeEdge> graph = new SimpleGraph<String, MaybeEdge>(
				MaybeEdge.class);

		AstNode fromexpression = params.get(1);
		AstNode whereexpression = params.get(2);
		AstNode returnexpression = params.get(3);

		if (!makeGraph((AstLiteralList) params.get(0), null, graph, context,
				error)) {
			return null;
		}

		Set<MaybeEdge> presentedges = new HashSet<MaybeEdge>();
		for (MaybeEdge edge : graph.edgeSet()) {
			if (edge.isPresent()) {
				presentedges.add(edge);
			}
		}
		if (!new ConnectivityInspector<String, MaybeEdge>(
				new UndirectedSubgraph<String, MaybeEdge>(graph, graph
						.vertexSet(), presentedges)).isGraphConnected()) {
			error.push(new ExpressionError(context,
					"Graph defined in expression is not connected.", null));
			return null;
		}
		return new AstGraph1(graph, fromexpression, whereexpression,
				returnexpression);
	}

	@Override
	protected String getInfo() {
		return "Subgraph Iterator";
	}

	@Override
	protected char[] getOperators() {
		return null;
	}

	@Override
	public Order getParsingOrder() {
		return Order.Tokens;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	private boolean makeGraph(AstLiteralList list, GraphNode parent,
			SimpleGraph<String, MaybeEdge> graph, ExpressionContext context,
			Stack<ExpressionError> error) {
		for (int index = 0; index < list.size(); index += 3) {
			String name = ((AstName) list.get(index + 1)).getName();

			GraphNode self;
			if (name.equals("K")) {
				self = new CompleteGraph(graph);
			} else if (name.equals("C")) {
				self = new CycleGraph(graph);
			} else {
				self = new ParentNode(name, graph);
			}
			if (list.get(index + 2) != null) {
				if (!makeGraph((AstLiteralList) list.get(index + 2), self,
						graph, context, error)) {
					return false;
				}
			}
			if (!self.finish(context, error)) {
				return false;
			}
			if (parent != null) {
				if (!parent.addChild(self, !(Boolean) ((AstLiteral) list
						.get(index)).getValue(), context, error)) {
					return false;
				}
			}
		}
		return true;
	}

}
