package ca.wlu.gisql.environment.parser.list;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.Show;

public interface ListParseable extends Show {

	public abstract boolean construct(Environment environment,
			List<AstNode> params, Stack<String> error, List<AstNode> results);

	public abstract Token[] tasks(Parser parser);

}
