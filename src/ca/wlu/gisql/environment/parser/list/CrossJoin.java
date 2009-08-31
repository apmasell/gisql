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

public class CrossJoin implements ListParseable {

	private final Token[] tokens = new Token[] {
			Literal.get('['),
			new Name(),
			Literal.get(','),
			new Name(),
			Literal.get('.'),
			new TemporaryEnvironment(1, new TemporaryEnvironment(0,
					Expression.self)), Literal.get(':'),
			ListExpression.instance, Literal.get(','), ListExpression.instance,
			Literal.get(']') };

	public boolean construct(Environment environment, List<AstNode> params,
			Stack<String> error, List<AstNode> results) {
		/* String outername = (String) params.get(0); */
		/* String innername = (String) params.get(1); */
		AstNode expression = params.get(2);
		AstList outerlist = (AstList) params.get(3);
		AstList innerlist = (AstList) params.get(4);

		AstList output = new AstList();
		for (AstNode outersubstitute : outerlist) {
			AstNode partialexpression = expression.fork(outersubstitute);
			if (partialexpression == null)
				return false;
			for (AstNode innersubstitute : innerlist) {
				AstNode completeexpression = partialexpression
						.fork(innersubstitute);
				if (completeexpression == null)
					return false;
				output.add(completeexpression);

			}
		}
		results.add(output);
		return true;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print
				.print("Cartesian Product: [ variable1 , variable2 . expression : outerlist, innerlist ]");
	}

	public Token[] tasks() {
		return tokens;
	}

}
