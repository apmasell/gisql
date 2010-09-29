package ca.wlu.gisql.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.OptionalMaybeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * An {@link AstNode} that is a list of other {@link AstNode}. This corresponds
 * to a literal list in the syntax. This can also be used in Java context since
 * it implement the List interface.
 */
public class AstLiteralList extends AstNode implements List<AstNode> {

	private final Type contents = new OptionalMaybeType(new TypeVariable());

	private final ExpressionContext context;

	private final List<AstNode> list = new ArrayList<AstNode>();

	private final Type type = new ListType(contents);

	public AstLiteralList() {
		this(null);
	}

	public AstLiteralList(ExpressionContext context) {
		super();
		this.context = context;
	}

	public boolean add(AstNode o) {
		return list.add(o);
	}

	public void add(int index, AstNode element) {
		list.add(index, element);
	}

	public boolean addAll(Collection<? extends AstNode> c) {
		return list.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends AstNode> c) {
		return list.addAll(index, c);
	}

	public void clear() {
		list.clear();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public boolean equals(Object o) {
		return list.equals(o);
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		for (AstNode node : list) {
			node.freeVariables(variables);
		}
	}

	public AstNode get(int index) {
		return list.get(index);
	}

	@Override
	public ResolutionEnvironment getModifiedEnvironment(
			ResolutionEnvironment environment) {
		for (AstNode node : this) {
			environment = node.getModifiedEnvironment(environment);
		}
		return environment;
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return list.hashCode();
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<AstNode> iterator() {
		return list.iterator();
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<AstNode> listIterator() {
		return list.listIterator();
	}

	public ListIterator<AstNode> listIterator(int index) {
		return list.listIterator(index);
	}

	public AstNode remove(int index) {
		return list.remove(index);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		return program.g$hO_CreateList(list);
	}

	@Override
	public void resetType() {
		contents.reset();
		for (AstNode node : list) {
			node.resetType();
		}
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		for (int index = 0; index < list.size(); index++) {
			AstNode result = list.get(index).resolve(runner,
					this.context == null ? context : this.context, environment);
			if (result == null) {
				return null;
			} else {
				list.set(index, result);
			}
		}
		return this;
	}

	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	public AstNode set(int index, AstNode element) {
		return list.set(index, element);
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("[");
		boolean first = true;
		for (AstNode node : this) {
			if (first) {
				first = false;
			} else {
				print.print(", ");
			}
			if (node == null) {
				print.print("∅");
			} else {
				print.print(node);
			}
		}
		print.print("]");
	}

	public int size() {
		return list.size();
	}

	public AstLiteralList subList(int start, int end) {
		AstLiteralList sublist = new AstLiteralList();
		sublist.list.addAll(list.subList(start, end));
		return sublist;
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	/** Ensure that all the items in this list have the correct type. */
	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		for (AstNode node : list) {
			if (!(node.type(runner, context) && contents.unify(node.getType()))) {
				runner.appendTypeError(node.getType(), contents, this, context);
				return false;
			}
		}
		return true;
	}
}
