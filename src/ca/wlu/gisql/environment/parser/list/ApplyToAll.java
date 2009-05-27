package ca.wlu.gisql.environment.parser.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Expression;
import ca.wlu.gisql.environment.parser.ListExpression;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.NextTask;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.TemporaryEnvironment;
import ca.wlu.gisql.environment.parser.Word;
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

	public NextTask[] tasks(Parser parser) {
		Name name = new Name(parser);
		return new NextTask[] { new Word(parser, "map"), name,
				new TemporaryEnvironment(parser, name, new Expression(parser)),
				new ListExpression(parser) };
	}
}
