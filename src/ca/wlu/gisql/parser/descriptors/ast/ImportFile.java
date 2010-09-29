package ca.wlu.gisql.parser.descriptors.ast;

import java.io.File;
import java.util.List;
import java.util.Stack;
import java.util.Map.Entry;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenQuotedString;
import ca.wlu.gisql.parser.TokenReservedWord;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.runner.FileContext;
import ca.wlu.gisql.util.Precedence;

public class ImportFile extends Parseable<AstNode, Precedence> {

	public static final Parseable<AstNode, Precedence> descriptor = new ImportFile();

	private ImportFile() {
		super(new TokenReservedWord<AstNode, Precedence>("import"),
				TokenQuotedString.self);
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
			Entry<AstNode, FileContext> result = runner.parseFile(file);
			return result == null ? null : result.getKey();
		} else {
			error.push(new ExpressionError(context,
					"I cannot contain place-holders.", null));
			return null;
		}
	}

	@Override
	protected String getInfo() {
		return "Import other script";
	}

	@Override
	protected char[] getOperators() {
		return null;
	}

	@Override
	public Order getParsingOrder() {
		return Order.Tokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Assignment;
	}
}
