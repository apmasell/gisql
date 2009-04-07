package ca.wlu.gisql.util;

import java.util.List;

import ca.wlu.gisql.Environment;
import ca.wlu.gisql.interactome.Interactome;

public interface Parseable extends Show {

    public enum NextTask {
	Double, Identifier, Maybe, Name, QuotedString, SubExpression
    }

    public abstract Interactome construct(Environment environment, List<Object> params);

    public abstract int getNestingLevel();

    public abstract boolean isMatchingOperator(char c);

    public abstract boolean isPrefixed();

    public abstract NextTask[] tasks();
}