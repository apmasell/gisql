package ca.wlu.gisql.environment.parser;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Show;

public interface Parseable extends Show {

	public abstract Interactome construct(Environment environment,
			List<Object> params, Stack<String> error);

	public abstract int getNestingLevel();

	public abstract boolean isMatchingOperator(char c);

	public abstract boolean isPrefixed();

	public abstract Token[] tasks(Parser parser);
}