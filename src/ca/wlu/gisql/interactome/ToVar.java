package ca.wlu.gisql.interactome;

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

    public StringBuilder show(StringBuilder sb) {
	i.show(sb);
	sb.append(" ≝ ");
	sb.append(varname);
	return sb;
    }

}
