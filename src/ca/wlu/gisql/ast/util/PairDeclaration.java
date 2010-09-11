package ca.wlu.gisql.ast.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstParameter;
import ca.wlu.gisql.ast.NamedVariable;
import ca.wlu.gisql.ast.type.PairType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class PairDeclaration extends ParameterDeclaration {

	private final ParameterDeclaration left;
	private final ParameterDeclaration right;
	private final Type type;

	public PairDeclaration(ParameterDeclaration left, ParameterDeclaration right) {
		this.left = left;
		this.right = right;
		type = new PairType(left.getType(), right.getType());
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.UnaryPostfix;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public boolean hasType() {
		return left.hasType() || right.hasType();
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(left, Precedence.Value);
		print.print('*');
		print.print(right, Precedence.UnaryPostfix);
	}

	@Override
	protected void synthesise(Set<NamedVariable> paramemters,
			List<Boolean> selectors, String parent) {
		selectors.add(true);
		left.synthesise(paramemters, selectors, parent);
		selectors.set(selectors.size() - 1, false);
		right.synthesise(paramemters, selectors, parent);
		selectors.remove(selectors.size() - 1);
	}

	@Override
	public AstParameter synthesize() {
		String name = "_" + hashCode();
		Set<NamedVariable> subordinates = new HashSet<NamedVariable>();
		synthesise(subordinates, new ArrayList<Boolean>(), name);

		return new AstParameter(name, type, subordinates);
	}

}
