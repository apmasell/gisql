package ca.wlu.gisql.environment.parser.list;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Expression;
import ca.wlu.gisql.environment.parser.ListExpression;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.TemporaryEnvironment;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class ApplyToAll implements ListParseable {

	public boolean construct(Environment environment, List<AstNode> params,
			Stack<String> error, List<AstNode> results) {
		/* AstString name = (AstString) params.get(0); */
		AstNode expression = params.get(1);
		AstList list = (AstList) params.get(2);

		AstList output = new AstList();
		for (AstNode substitute : list) {
			AstNode result = expression.fork(substitute);
			if (result == null)
				return false;
			output.add(result);
		}
		results.add(output);
		return true;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("List Comprehension: [ variable . expression : list ]");
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
