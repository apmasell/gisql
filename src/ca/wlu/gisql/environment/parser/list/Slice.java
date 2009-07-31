package ca.wlu.gisql.environment.parser.list;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.ListExpression;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.Maybe;
import ca.wlu.gisql.environment.parser.Number;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.Sequence;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.Word;
import ca.wlu.gisql.environment.parser.ast.AstInteger;
import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class Slice implements ListParseable {

	private static final Token[] tokens = new Token[] { new Word("slice"),
			Literal.get('('), Number.self, Literal.get(','),
			new Maybe(new Sequence(Number.self, Literal.get(','))),
			ListExpression.self, Literal.get(')') };

	public boolean construct(Environment environment, List<AstNode> params,
			Stack<String> error, List<AstNode> results) {
		/*
		 * Our lists are 1-based and inclusive, and Java's are 0-based,
		 * inclusive on the start and exclusive on the end. Negative indicies on
		 * the end.
		 */
		int start = ((AstInteger) params.get(0)).getInt();
		AstInteger endNode = (AstInteger) params.get(1);
		Integer end = (endNode == null ? null : endNode.getInt());
		AstList list = (AstList) params.get(2);

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

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("slice(start, [ end,] list)");
	}

	public Token[] tasks() {
		return tokens;
	}
}
