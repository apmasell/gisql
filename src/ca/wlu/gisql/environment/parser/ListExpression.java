package ca.wlu.gisql.environment.parser;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.list.ApplyToAll;
import ca.wlu.gisql.environment.parser.list.CrossJoin;
import ca.wlu.gisql.environment.parser.list.FromFile;
import ca.wlu.gisql.environment.parser.list.ListParseable;
import ca.wlu.gisql.environment.parser.list.RawList;
import ca.wlu.gisql.environment.parser.list.Slice;
import ca.wlu.gisql.environment.parser.list.ToVar;
import ca.wlu.gisql.environment.parser.list.Variable;
import ca.wlu.gisql.environment.parser.list.Zip;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class ListExpression extends Token {

	private static final ListParseable[] operators = new ListParseable[] {
			new ApplyToAll(), new CrossJoin(), new FromFile(), new RawList(),
			new Slice(), new ToVar(), new Variable(), new Zip() };

	public static void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		for (ListParseable operator : operators) {
			print.println(operator);
		}
	}

	private final Parser parser;

	public ListExpression(Parser parser) {
		this.parser = parser;
	}

	boolean parse(int level, List<AstNode> results) {
		for (ListParseable operator : operators) {
			int oldposition = parser.position;
			if (processOperator(operator, level, results)) {
				return true;
			}
			parser.position = oldposition;
		}
		return false;
	}

	private boolean processOperator(ListParseable operator, int level,
			List<AstNode> results) {
		Token[] todo = operator.tasks(this.parser);
		List<AstNode> params = new ArrayList<AstNode>();
		int errorstate = parser.error.size();

		for (Token task : todo) {
			this.parser.consumeWhitespace();
			if (!task.parse(level, params)) {
				parser.error.setSize(errorstate);
				return false;
			}
		}
		return operator.construct(this.parser.environment, params,
				this.parser.error, results);
	}
}