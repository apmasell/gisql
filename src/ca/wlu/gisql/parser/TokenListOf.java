package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Finds a list of tokens delimited by a specific character. Whitespace may
 * appear around the delimiter. This must match at least on token. That is, it
 * behaves like the regular expression + operator.
 */
public class TokenListOf extends Token<AstNode, Precedence> {
	private final Token<AstNode, Precedence> child;

	private final Character delimiter;

	public TokenListOf(Character delimiter,
			Token<AstNode, Precedence>... tokens) {
		this(new TokenSequence<AstNode, Precedence>(tokens), delimiter);
	}

	public TokenListOf(Token<AstNode, Precedence> child, Character delimiter) {
		super();
		this.child = child;
		this.delimiter = delimiter;
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
		child.addReservedWords(reservedwords);
	}

	@Override
	boolean parse(ParserKnowledgebase<AstNode, Precedence> knowledgebase,
			Parser parser, Precedence level, List<AstNode> results) {
		AstLiteralList items = new AstLiteralList();

		if (!child.parse(knowledgebase, parser, level, items)) {
			return false;
		}

		parser.consumeWhitespace();
		while (parser.hasMore()) {
			if (delimiter == null) {
				if (!child.parse(knowledgebase, parser, level, items)) {
					results.add(items);
					return true;
				}
			} else if (parser.peek() == delimiter) {
				parser.next();
				parser.consumeWhitespace();
				if (!child.parse(knowledgebase, parser, level, items)) {
					return false;
				}
			} else {
				results.add(items);
				return true;
			}
			parser.consumeWhitespace();
		}
		results.add(items);
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print(child);
		if (delimiter != null) {
			print.print('[');
			print.print(delimiter);
			print.print("] ...");
		}
	}
}