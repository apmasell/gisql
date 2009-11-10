package ca.wlu.gisql.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.vm.InstructionPack;

/**
 * An {@link AstNode} that is a list of other {@link AstNode}. This corresponds
 * to a literal list in the syntax.
 */
public class AstList extends AstNode implements List<AstNode> {

	private final TypeVariable contents = new TypeVariable();

	private final List<AstNode> list = new ArrayList<AstNode>();

	private final Type type = new ListType(contents);

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

	public AstNode get(int index) {
		return list.get(index);
	}

	@Override
	protected int getNeededParameterCount() {
		return 0;
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
	public boolean render(ProgramRoutine program, int depth, int debrujin) {
		for (AstNode node : list) {
			if (!node.render(program, 0, debrujin)) {
				return false;
			}
		}
		return program.instructions.add(new InstructionPack(list.size()));
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
			Environment environment) {
		for (int index = 0; index < list.size(); index++) {
			list.set(index, list.get(index).resolve(runner, context,
					environment));
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
			print.print(node);
		}
		print.print("]");
	}

	public int size() {
		return list.size();
	}

	public AstList subList(int start, int end) {
		AstList sublist = new AstList();
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
