package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.NativeType;
import ca.wlu.gisql.ast.util.ParameterDeclaration;
import ca.wlu.gisql.parser.descriptors.DeclarationNesting;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class TokenDeclaration extends Token<AstNode, Precedence> {

	public static final TokenDeclaration self = new TokenDeclaration();

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(ParserKnowledgebase<AstNode, Precedence> knowledgebase,
			Parser parser, Precedence level, List<AstNode> results) {
		ParameterDeclaration declaration = parser.parseAutoExpression(
				new DeclarationKnowledgeBase(), DeclarationNesting.Pair);
		if (declaration == null) {
			return false;
		} else {
			results.add(new AstLiteral(NativeType.AmbiguousJavaType,
					declaration));
			return true;
		}
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print("<type>");
	}

}
