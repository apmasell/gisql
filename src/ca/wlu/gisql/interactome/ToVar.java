package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.List;

import ca.wlu.gisql.Environment;
import ca.wlu.gisql.util.Parseable;

public class ToVar extends AbstractShadowInteractome {
    public final static Parseable descriptor = new Parseable() {

	public Interactome construct(Environment environment,
		List<Object> params) {
	    Interactome interactome = (Interactome) params.get(0);
	    String name = (String) params.get(1);
	    if (name == null)
		return null;
	    return new ToVar(environment, interactome, name);
	}

	public int getNestingLevel() {
	    return 0;
	}

	public boolean isMatchingOperator(char c) {
	    return c == '@';
	}

	public boolean isPrefixed() {
	    return false;
	}

	public PrintStream show(PrintStream print) {
	    print.print("Assign to variable\tA @ varname");
	    return null;
	}

	public StringBuilder show(StringBuilder sb) {
	    sb.append("Assign to variable\tA @ varname");
	    return sb;
	}

	public NextTask[] tasks() {
	    return new NextTask[] { NextTask.Name };
	}

    };

    private Environment env;

    private String varname;

    public ToVar(Environment env, Interactome i, String varname) {
	super();
	this.env = env;
	this.i = i;
	this.varname = varname;
    }

    public void postprocess() {
	env.setVariable(varname, i);
    }

    public PrintStream show(PrintStream print) {
	i.show(print);
	print.print(" @ ");
	print.print(varname);
	return print;
    }

    public StringBuilder show(StringBuilder sb) {
	i.show(sb);
	sb.append(" @ ");
	sb.append(varname);
	return sb;
    }

}
