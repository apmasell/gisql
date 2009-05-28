package ca.wlu.gisql.environment.parser.list;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.ListExpression;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.Maybe;
import ca.wlu.gisql.environment.parser.NextTask;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.Sequence;
import ca.wlu.gisql.environment.parser.Word;
import ca.wlu.gisql.interactome.Interactome;

public class Slice implements ListParseable {

	@SuppressWarnings("unchecked")
	public boolean construct(Environment environment, List<Object> params,
			Stack<String> error, List<Object> results) {
		/*
		 * Our lists are 1-based and inclusive, and Java's are 0-based,
		 * inclusive on the start and exclusive on the end. Negative indicies on
		 * the end.
		 */
		int start = (Integer) params.get(0);
		Integer end = (Integer) params.get(1);
		List<Interactome> list = (List<Interactome>) params.get(2);

		/* Fix 0-base. */
		start--;

		if (end == null)
			end = list.size();

		/* Fix negative-based indicies. */
		if (end <= 0)
			end += list.size();

		if (start < 0 || start >= list.size()) {
			error.push("Start index out of range.");
			return false;
		}
		if (end <= start || end > list.size()) {
			error.push("End index out of range.");
			return false;
		}

		results.add(list.subList(start, end));
		return true;
	}

	public NextTask[] tasks(Parser parser) {
		return new NextTask[] {
				new Word(parser, "slice"),
				new Literal(parser, '('),
				new ca.wlu.gisql.environment.parser.Number(parser),
				new Literal(parser, ','),
				new Maybe(parser, new Sequence(
						new ca.wlu.gisql.environment.parser.Number(parser),
						new Literal(parser, ','))), new ListExpression(parser),
				new Literal(parser, ')') };
	}

	public PrintStream show(PrintStream print) {
		print.print("slice(start, [ end,] list)");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("slice(start, [ end,] list)");
		return sb;
	}
}
