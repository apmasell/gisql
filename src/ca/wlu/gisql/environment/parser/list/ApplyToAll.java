package ca.wlu.gisql.environment.parser.list;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Expression;
import ca.wlu.gisql.environment.parser.ListExpression;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.TemporaryEnvironment;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.interactome.Interactome;

public class ApplyToAll implements ListParseable {

	@SuppressWarnings("unchecked")
	public boolean construct(Environment environment, List<Object> params,
			Stack<String> error, List<Object> results) {
		/* String name = (String) params.get(0); */
		Interactome expression = (Interactome) params.get(1);
		List<Interactome> list = (List<Interactome>) params.get(2);
		if (!expression.needsFork())
			return false;

		List<Interactome> output = new ArrayList<Interactome>();
		for (Interactome substitute : list) {
			output.add(expression.fork(substitute));
		}
		results.add(output);
		return true;
	}

	public PrintStream show(PrintStream print) {
		print.print("List Comprehension: [ variable . expression : list ]");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("List Comprehension: [ variable . expression : list ]");
		return sb;
	}

	public Token[] tasks(Parser parser) {
		Name name = new Name(parser);
		return new Token[] { new Literal(parser, '['), name,
				new Literal(parser, '.'),
				new TemporaryEnvironment(parser, name, new Expression(parser)),
				new Literal(parser, ':'), new ListExpression(parser),
				new Literal(parser, ']') };
	}
}
