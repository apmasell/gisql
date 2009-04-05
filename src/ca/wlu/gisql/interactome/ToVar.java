package ca.wlu.gisql.interactome;

import java.io.PrintStream;

import ca.wlu.gisql.Environment;

public class ToVar extends AbstractShadowInteractome {
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
	print.print(" ≝ ");
	print.print(varname);
	return print;
    }

    public StringBuilder show(StringBuilder sb) {
	i.show(sb);
	sb.append(" ≝ ");
	sb.append(varname);
	return sb;
    }

}
