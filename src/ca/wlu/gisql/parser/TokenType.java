package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.parser.descriptors.type.TypeNesting;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class TokenType extends Token<AstNode, Precedence> {

	public static final TokenType self = new TokenType();

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(ParserKnowledgebase<AstNode, Precedence> knowledgebase,
			Parser parser, Precedence level, List<AstNode> results) {
		Type type = parser.parseAutoExpression(new TypeKnowledgeBase(),
				TypeNesting.Arrow);
		if (type == null) {
			return false;
		} else {
			results.add(new AstLiteral(Type.TypeType, type));
			return true;
		}
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print("<type>");
	}

}
