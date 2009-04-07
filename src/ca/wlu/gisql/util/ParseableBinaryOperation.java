package ca.wlu.gisql.util;

import java.io.PrintStream;
import java.util.List;

import org.apache.log4j.Logger;

import ca.wlu.gisql.Environment;
import ca.wlu.gisql.interactome.ArithmeticInteractome;
import ca.wlu.gisql.interactome.Interactome;

public class ParseableBinaryOperation implements Parseable {
    static final Logger log = Logger.getLogger(ParseableBinaryOperation.class);

    private char[] alternateoperators;

    private Class<? extends ArithmeticInteractome> implementation;

    private String name;

    private int nestinglevel;

    private char symbol;

    public ParseableBinaryOperation(
	    Class<? extends ArithmeticInteractome> implementation,
	    int nestinglevel, char symbol, char[] alternateoperators,
	    String name) {
	super();
	this.implementation = implementation;
	this.nestinglevel = nestinglevel;
	this.symbol = symbol;
	this.alternateoperators = alternateoperators;
	this.name = name;
    }

    public Interactome construct(Environment environment, List<Object> params) {
	Interactome left = (Interactome) params.get(0);
	Interactome right = (Interactome) params.get(1);

	try {
	    return implementation.getConstructor(Interactome.class,
		    Interactome.class).newInstance(left, right);
	} catch (Exception e) {
	    log.error("Instatiation error during parsing.", e);
	}
	return null;
    }

    public int getNestingLevel() {
	return nestinglevel;
    }

    public char getSymbol() {
	return symbol;
    }

    public boolean isMatchingOperator(char c) {
	if (symbol == c)
	    return true;
	if (alternateoperators == null)
	    return false;
	for (char operator : alternateoperators)
	    if (operator == c)
		return true;

	return false;
    }

    public boolean isPrefixed() {
	return false;
    }

    public PrintStream show(PrintStream print) {
	print.print(name);
	print.print(": A ");
	print.print(symbol);
	print.print(" B");
	if (alternateoperators != null) {
	    for (char c : alternateoperators) {
		print.print(", A ");
		print.print(c);
		print.print(" B");
	    }
	}
	return print;
    }

    public StringBuilder show(StringBuilder sb) {
	sb.append(name);
	sb.append(": A ");
	sb.append(symbol);
	sb.append(" B");
	if (alternateoperators != null) {
	    for (char c : alternateoperators) {
		sb.append(", A ");
		sb.append(c);
		sb.append(" B");
	    }
	}
	return sb;
    }

    public NextTask[] tasks() {
	return new NextTask[] { NextTask.SubExpression };
    }

}
