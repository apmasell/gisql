package ca.wlu.gisql.environment.parser.list;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.NextTask;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.interactome.Interactome;

public class Variable implements ListParseable {

	public boolean construct(Environment environment, List<Object> params,
			Stack<String> error, List<Object> results) {
		String name = (String) params.get(0);
		List<Interactome> list = environment.getArray(name);
		if (list == null) {
			return false;
		} else {
			results.add(list);
			return true;
		}
	}

	public PrintStream show(PrintStream print) {
		print.print("Variable: name");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("Variable: name");
		return sb;
	}

	public NextTask[] tasks(Parser parser) {
		return new NextTask[] { new Name(parser) };
	}

}
