package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNative;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.BuiltInResolver;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** This operator extracts the help information from a function and returns it. */
public class HelpDescriptor implements Parseable {

	public static final Parseable descriptor = new HelpDescriptor();

	private static final Token[] tokens = new Token[] { TokenName.self };

	private HelpDescriptor() {
	}

	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {

		String name = ((AstName) params.get(0)).getName();
		Object variable = runner.getEnvironment().getVariable(name);
		Type type = runner.getEnvironment().getTypeOf(name);
		AstNative node = BuiltInResolver.get(name);

		String result;
		if (node != null) {
			result = name + " :: " + node.getType() + "\n\t"
					+ node.getDescription();
		} else if (variable == null && type != null) {
			result = "undefined :: " + type;
		} else if (variable instanceof GenericFunction) {
			GenericFunction function = (GenericFunction) variable;
			result = function + " :: " + type + "\n\t"
					+ function.getDescription();
		} else if (type == null && node == null) {
			result = "unknown name";
		} else {
			result = variable + " :: " + type;
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
