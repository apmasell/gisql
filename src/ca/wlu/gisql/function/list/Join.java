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

public class Join extends Function {
	private static final TypeVariable a = new TypeVariable();

	private static final ListType alist = new ListType(a);

	public static final Parseable<AstNode, Precedence> descriptor = new JoinDescriptor();

	public Join(ExpressionRunner runner) {
		super(runner, "join", "Concatenates two lists", alist, alist, alist);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Object... parameters) {
		List result = new ArrayList();
		result.addAll((List) parameters[0]);
		result.addAll((List) parameters[1]);
		return result;
	}

}
