package ca.wlu.gisql.util;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.Environment;
import ca.wlu.gisql.Parser;
import ca.wlu.gisql.interactome.Interactome;

public interface Parseable extends Show {

	public abstract Interactome construct(Environment environment,
			List<Object> params, Stack<String> error);

	public abstract int getNestingLevel();

	public abstract boolean isMatchingOperator(char c);

	public abstract boolean isPrefixed();

	public abstract Parser.NextTask[] tasks(Parser parser);
}