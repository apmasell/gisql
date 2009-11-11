package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNative;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** This operator extracts the help information from a function and returns it. */
public class HelpDescriptor implements Parseable {

	public static final Parseable descriptor = new HelpDescriptor();

	private static final Token[] tokens = new Token[] { TokenName.self };

	private HelpDescriptor() {
	}

	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode variable = environment.getVariable(((AstName) params.get(0))
				.getName());
		String result;
		if (variable == null) {
			result = "undefined name";
		} else if (variable instanceof AstNative) {
			AstNative function = (AstNative) variable;
			result = function.getName() + " :: " + function.getType() + "\n\t"
					+ function.getDescription();
		} else if (variable instanceof AstLiteral) {
			AstLiteral literal = (AstLiteral) variable;
			result = literal.toString() + " :: " + literal.getType();
		} else {
			result = variable.toString();
		}
		return new AstLiteral(Type.StringType, result);
	}

	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	public boolean isMatchingOperator(char c) {
		return c == '?';
	}

	public Boolean isPrefixed() {
		return true;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.println("Help: ? name");
	}

	public Token[] tasks() {
		return tokens;
	}

}
