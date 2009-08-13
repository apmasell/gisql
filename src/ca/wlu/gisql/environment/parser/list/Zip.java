package ca.wlu.gisql.environment.parser.list;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Expression;
import ca.wlu.gisql.environment.parser.ListExpression;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.TemporaryEnvironment;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class Zip implements ListParseable {

	private final Token[] tokens = new Token[] {
			Literal.get('<'),
			new Name(),
			Literal.get(','),
			new Name(),
			Literal.get('.'),
			new TemporaryEnvironment(1, new TemporaryEnvironment(0,
					Expression.self)), Literal.get(':'), ListExpression.instance,
			Literal.get(','), ListExpression.instance, Literal.get('>') };

	public boolean construct(Environment environment, List<AstNode> params,
			Stack<String> error, List<AstNode> results) {
		/* AstString outername = (AstString) params.get(0); */
		/* AstString innername = (AstString) params.get(1); */
		AstNode expression = params.get(2);
		AstList leftlist = (AstList) params.get(3);
		AstList rightlist = (AstList) params.get(4);

		AstList output = new AstList();
		int upperbound = Math.min(leftlist.size(), rightlist.size());
		for (int index = 0; index < upperbound; index++) {
			AstNode partialexpression = expression.fork(leftlist.get(index));
			if (partialexpression == null)
				return false;
			AstNode completeexpression = partialexpression.fork(rightlist
					.get(index));
			if (completeexpression == null)
				return false;
			output.add(completeexpression);

		}
		results.add(output);
		return true;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print
				.print("Zip: < variable1 , variable2 . expression : list1, list2 >");
	}

	public Token[] tasks() {
		return tokens;
	}

}
