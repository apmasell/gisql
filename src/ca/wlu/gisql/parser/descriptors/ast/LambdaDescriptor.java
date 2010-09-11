package ca.wlu.gisql.parser.descriptors.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLambda1;
import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.ParameterDeclaration;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenDeclaration;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.parser.TokenMaybe;
import ca.wlu.gisql.parser.TokenType;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowableStringBuilder;

/**
 * This operator parses anonymous functions (aka lambdas). The syntax ('var
 * expression) was chosen because \ is used for set division, so Haskell's (\var
 * -> expression), was not possible and LISP/Scheme (lambda var expression) is
 * bulky.
 */
public class LambdaDescriptor extends Parseable<AstNode, Precedence> {

	public static final Parseable<AstNode, Precedence> descriptor = new LambdaDescriptor();

	private LambdaDescriptor() {
		super(TokenDeclaration.self, new TokenMaybe<AstNode, Precedence>(
				new TokenMatchCharacter<AstNode, Precedence>("::"),
				TokenType.self), new TokenMatchCharacter<AstNode, Precedence>(
				"->"), TokenExpressionRight.<AstNode, Precedence> get());
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		ParameterDeclaration declaration = (ParameterDeclaration) ((AstLiteral) params
				.get(0)).getValue();
		Type type = params.get(1) == null ? null : (Type) ((AstLiteral) params
				.get(1)).getValue();
		AstNode expression = params.get(2);
		if (type != null && !type.unify(declaration.getType())) {
			ShowableStringBuilder<List<TypeVariable>> print = new ShowableStringBuilder<List<TypeVariable>>(
					new ArrayList<TypeVariable>());
			print.print("Got type \"");
			print.print(type);
			print.print("\" expected \"");
			print.print(declaration.getType());
			print.print("'' when specifying type explicitly.");
			error.add(new ExpressionError(context, print.toString(), null));
			print.close();
			return null;
		}
		return new AstLambda1(declaration, expression);
	}

	@Override
	protected String getInfo() {
		return "Anonymous function";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '\\' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Closure;
	}
}
