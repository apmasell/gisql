package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.Prioritizable;
import ca.wlu.gisql.util.Show;

public interface Parseable extends Prioritizable<ParserKnowledgebase>,
		Show<ParserKnowledgebase> {

	public abstract AstNode construct(UserEnvironment environment,
			List<AstNode> params, Stack<ExpressionError> error,
			ExpressionContext context);

	public abstract boolean isMatchingOperator(char c);

	public abstract boolean isPrefixed();

	public abstract Token[] tasks();
}