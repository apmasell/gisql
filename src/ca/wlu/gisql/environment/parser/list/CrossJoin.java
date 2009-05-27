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
import ca.wlu.gisql.environment.parser.NextTask;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.TemporaryEnvironment;
import ca.wlu.gisql.interactome.Interactome;

public class CrossJoin implements ListParseable {

	@SuppressWarnings("unchecked")
	public boolean construct(Environment environment, List<Object> params,
			Stack<String> error, List<Object> results) {
		/* String outername = (String) params.get(0); */
		/* String innername = (String) params.get(1); */
		Interactome expression = (Interactome) params.get(2);
		List<Interactome> outerlist = (List<Interactome>) params.get(3);
		List<Interactome> innerlist = (List<Interactome>) params.get(4);
		if (!expression.needsFork())
			return false;

		List<Interactome> output = new ArrayList<Interactome>();
		for (Interactome outersubstitute : outerlist) {
			Interactome partialexpression = expression.fork(outersubstitute);
			if (!partialexpression.needsFork())
				return false;
			for (Interactome innersubstitute : innerlist) {
				output.add(partialexpression.fork(innersubstitute));

			}
		}
		results.add(output);
		return true;
	}

	public PrintStream show(PrintStream print) {
		print
				.print("Cartesian Product: [ variable1 , variable 2 . expression : outerlist, innerlist ]");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb
				.append("Cartesian Product: [ variable1 , variable 2 . expression : outerlist, innerlist ]");
		return sb;
	}

	public NextTask[] tasks(Parser parser) {
		Name outername = new Name(parser);
		Name innername = new Name(parser);
		return new NextTask[] {
				new Literal(parser, '['),
				outername,
				new Literal(parser, ','),
				innername,
				new Literal(parser, '.'),
				new TemporaryEnvironment(parser, outername,
						new TemporaryEnvironment(parser, innername,
								new Expression(parser))),
				new Literal(parser, ':'), new ListExpression(parser),
				new ListExpression(parser), new Literal(parser, ']') };
	}

}
