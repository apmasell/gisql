package ca.wlu.gisql.environment.parser;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.Prioritizable;
import ca.wlu.gisql.util.Show;

public interface Parseable extends Prioritizable<ParserKnowledgebase>,
		Show<ParserKnowledgebase> {

	public abstract AstNode construct(Environment environment,
			List<AstNode> params, Stack<String> error);

	public abstract boolean isMatchingOperator(char c);

	public abstract boolean isPrefixed();

	public abstract Token[] tasks(Parser parser);
}