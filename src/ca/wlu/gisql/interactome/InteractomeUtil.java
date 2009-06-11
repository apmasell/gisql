package ca.wlu.gisql.interactome;

import java.io.PrintStream;

public class InteractomeUtil {

	public static PrintStream precedenceShow(PrintStream print,
			Interactome interactome, int level) {
		if (interactome.getPrecedence() < level)
			print.print("(");
		interactome.show(print);
		if (interactome.getPrecedence() < level)
			print.print(")");
		return print;
	}

	public static StringBuilder precedenceShow(StringBuilder sb,
			Interactome interactome, int level) {
		if (interactome.getPrecedence() < level)
			sb.append("(");
		interactome.show(sb);
		if (interactome.getPrecedence() < level)
			sb.append(")");
		return sb;
	}

}
