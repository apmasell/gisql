package ca.wlu.gisql.ast.util;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstParameter;
import ca.wlu.gisql.ast.NamedVariable;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.Prioritizable;
import ca.wlu.gisql.util.ShowableStringBuilder;

public abstract class ParameterDeclaration implements
		Prioritizable<AstNode, Precedence> {

	public abstract Type getType();

	public abstract boolean hasType();

	protected abstract void synthesise(Set<NamedVariable> paramemters,
			List<Boolean> selectors, String parent);

	public abstract AstParameter synthesize();

	@Override
	public final String toString() {
		return ShowableStringBuilder.toString(this, null);
	}

}
