package ca.wlu.gisql.function.list;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

public class Cons extends Function {
	private static final TypeVariable a = new TypeVariable();

	private static final ListType alist = new ListType(a);

	public static final Parseable<AstNode, Precedence> descriptor = new ConsDescriptor();

	public Cons(ExpressionRunner runner) {
		super(runner, "cons", "Adds an item to the beginning of a list", a,
				alist, alist);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Object... parameters) {
		List result = new ArrayList();
		result.add(parameters[0]);
		result.addAll((List) parameters[1]);
		return result;
	}

}
