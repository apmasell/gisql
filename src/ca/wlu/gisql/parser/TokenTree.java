package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class TokenTree extends Token<AstNode, Precedence> {

	private final Token<AstNode, Precedence> child;
	private final char close;
	private final char delimiter;
	private final char open;

	public TokenTree(char delimiter, char open, char close,
			Token<AstNode, Precedence> child) {
		this.delimiter = delimiter;
		this.open = open;
		this.close = close;
		this.child = child;
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
		child.addReservedWords(reservedwords);
	}

	@Override
	boolean parse(ParserKnowledgebase<AstNode, Precedence> knowledgebase,
			Parser parser, Precedence level, List<AstNode> results) {
		AstLiteralList items = new AstLiteralList();

		if (!parser.hasMore() || parser.peek() != open) {
			parser.pushError("Expected " + open);
			return false;
		}
		parser.next();
		parser.consumeWhitespace();
		if (!child.parse(knowledgebase, parser, level, items)) {
			return false;
		}

		parser.consumeWhitespace();
		while (parser.hasMore()) {
			if (parser.peek() == open) {
				parser.consumeWhitespace();
				if (!parse(knowledgebase, parser, level, items)) {
					return false;
				}
				parser.consumeWhitespace();
			} else {
				items.add(null);
			}
			if (parser.peek() == delimiter) {
				parser.next();
				parser.consumeWhitespace();
				if (!child.parse(knowledgebase, parser, level, items)) {
					return false;
				}
			} else if (parser.peek() == close) {
				parser.next();
				results.add(items);
				return true;
			} else {
				parser.pushError("Unknown character " + parser.peek());
				return false;
			}
			parser.consumeWhitespace();
		}
		parser.pushError("Expected " + close);
		return false;
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print('[');
		print.print(open);
		print.print(']');
		print.print(child);
		print.print('[');
		print.print(open);
		print.print("]...[");
		print.print(close);
		print.print("] [");
		print.print(delimiter);
		print.print("] ...");
		print.print('[');
		print.print(close);
		print.print(']');
	}
}
