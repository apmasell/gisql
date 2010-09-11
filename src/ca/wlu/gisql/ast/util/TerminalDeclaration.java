package ca.wlu.gisql.ast.util;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstPairAccessor;
import ca.wlu.gisql.ast.AstParameter;
import ca.wlu.gisql.ast.NamedVariable;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class TerminalDeclaration extends ParameterDeclaration {

	private final String name;
	private final Type type;
	private final boolean typespecified;

	public TerminalDeclaration(String name, Type type) {
		this.name = name;
		typespecified = type != null;
		this.type = typespecified ? type : new TypeVariable();

	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public boolean hasType() {
		return typespecified;
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(name);
	}

	@Override
	protected void synthesise(Set<NamedVariable> paramemters,
			List<Boolean> selectors, String parent) {
		boolean[] array = new boolean[selectors.size()];
		for (int index = 0; index < selectors.size(); index++) {
			array[index] = selectors.get(index);
		}
		paramemters.add(new AstPairAccessor(parent, name, type, array));
	}

	@Override
	public AstParameter synthesize() {
		return new AstParameter(name, type);
	}

}
