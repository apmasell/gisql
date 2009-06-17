package ca.wlu.gisql.environment.parser.list;

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
import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class CrossJoin implements ListParseable {

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

	public void show(ShowablePrintWriter print) {
		print
				.print("Cartesian Product: [ variable1 , variable2 . expression : outerlist, innerlist ]");
	}

	public Token[] tasks(Parser parser) {
		Name outername = new Name(parser);
		Name innername = new Name(parser);
		return new Token[] {
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
