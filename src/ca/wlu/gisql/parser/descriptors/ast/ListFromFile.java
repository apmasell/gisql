package ca.wlu.gisql.parser.descriptors.ast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.parser.TokenQuotedString;
import ca.wlu.gisql.parser.TokenReservedWord;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.runner.FileContext;
import ca.wlu.gisql.util.Precedence;

public class ListFromFile extends Parseable<AstNode, Precedence> {

	public static final Parseable<AstNode, Precedence> descriptor = new ListFromFile();

	private ListFromFile() {
		super(new TokenReservedWord<AstNode, Precedence>("from"),
				TokenQuotedString.self, TokenMatchCharacter
						.<AstNode, Precedence> get(']'));
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		if (params.get(0) instanceof AstLiteral) {
			File file = new File((String) ((AstLiteral) params.get(0))
					.getValue());
			if (!file.canRead()) {
				error.push(new ExpressionError(context, file
						+ " is not available for reading.", null));
				return null;
			}
			FileContext filecontext = new FileContext(file);
			AstLiteralList list = new AstLiteralList(filecontext);
			try {
				BufferedReader input = new BufferedReader(new FileReader(file));
				String line;
				int linenumber = 0;
				while ((line = input.readLine()) != null) {
					linenumber++;
					Parser parser = new Parser(runner, filecontext
							.getContextForLine(linenumber, line), line, runner
							.getListener());
					AstNode result = parser.parse();
					if (result == null) {
						return null;
					}
					list.add(result);
				}
				input.close();
			} catch (IOException e) {
				error.push(new ExpressionError(filecontext,
						"Error reading file.", e));
				return null;
			}
			return list;
		} else {
			error.push(new ExpressionError(context,
					"List from file cannot contain place-holders.", null));
			return null;
		}
	}

	@Override
	protected String getInfo() {
		return "List from file";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '[' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}
}
