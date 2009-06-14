package ca.wlu.gisql.environment.parser.list;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.ListExpression;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.interactome.Interactome;

public class ToVar implements ListParseable {

	@SuppressWarnings("unchecked")
	public boolean construct(Environment environment, List<Object> params,
			Stack<String> error, List<Object> results) {
		String name = (String) params.get(0);
		List<Interactome> array = (List<Interactome>) params.get(1);
		return environment.setArray(name, array) && results.add(array);
	}

	public PrintStream show(PrintStream print) {
		print.print("List assignment: name = list");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("List assignment: name = list");
		return sb;
	}

	public Token[] tasks(Parser parser) {
		return new Token[] { new Name(parser), new Literal(parser, '='),
				new ListExpression(parser) };
	}

}
