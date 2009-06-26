package ca.wlu.gisql.environment.parser.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class AstList extends ArrayList<AstNode> implements AstNode {

	private static final long serialVersionUID = 8036494028431778925L;

	public AstList() {
		super();
	}

	public AstList(Collection<Interactome> list) {
		super();
		for (Interactome interactome : list) {
			this.add(new AstInteractome(interactome));
		}
	}

	public Interactome asInteractome() {
		return null;
	}

	public List<Interactome> asInteractomeList() {
		List<Interactome> result = new ArrayList<Interactome>();
		for (AstNode node : this) {
			Interactome interactome = node.asInteractome();
			if (interactome == null)
				return null;
			result.add(interactome);
		}
		return result;
	}

	public AstNode fork(AstNode substitute) {
		AstList forked = new AstList();
		for (AstNode node : this) {
			forked.add(node.fork(substitute));
		}
		return forked;
	}

	public int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	public boolean isInteractome() {
		return false;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("{");
		boolean first = true;
		for (AstNode node : this) {
			if (first)
				first = false;
			else
				print.print(", ");
			node.show(print);
		}
		print.print("}");
	}

	public AstList subList(int start, int end) {
		AstList sublist = new AstList();
		sublist.addAll(super.subList(start, end));
		return sublist;
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, null);
	}
}
