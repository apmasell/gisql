package ca.wlu.gisql.environment.parser;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.environment.parser.list.ApplyToAll;
import ca.wlu.gisql.environment.parser.list.CrossJoin;
import ca.wlu.gisql.environment.parser.list.ListParseable;
import ca.wlu.gisql.environment.parser.list.RawList;
import ca.wlu.gisql.environment.parser.list.Zip;

public class ListExpression extends NextTask {

	private static final ListParseable[] operators = new ListParseable[] {
			new ApplyToAll(), new CrossJoin(), new RawList(), new Zip() };

	public static StringBuilder show(StringBuilder sb) {
		for (ListParseable operator : operators) {
			operator.show(sb);
			sb.append('\n');
		}
		return sb;
	}

	private final Parser parser;

	public ListExpression(Parser parser) {
		this.parser = parser;
	}

	boolean parse(int level, List<Object> results) {
		int errorstate = parser.error.size();
		for (ListParseable operator : operators) {
			int oldposition = parser.position;
			if (processOperator(operator, level, results)) {
				parser.error.setSize(errorstate);
				return true;
			}
			parser.position = oldposition;
		}
		return false;
	}

	private boolean processOperator(ListParseable operator, int level,
			List<Object> results) {
		NextTask[] todo = operator.tasks(this.parser);
		List<Object> params = new ArrayList<Object>();

		for (NextTask task : todo) {
			this.parser.consumeWhitespace();
			if (!task.parse(level, params)) {
				return false;
			}
		}
		return operator.construct(this.parser.environment, params,
				this.parser.error, results);
	}
}