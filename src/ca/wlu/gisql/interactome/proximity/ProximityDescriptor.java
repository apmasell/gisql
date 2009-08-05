package ca.wlu.gisql.interactome.proximity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.ListOf;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.Maybe;
import ca.wlu.gisql.environment.parser.Number;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.Word;
import ca.wlu.gisql.environment.parser.ast.AstInteger;
import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class ProximityDescriptor implements Parseable {

	private static final Token[] tokens = new Token[] { new Word("near"),
			Literal.get('('), new ListOf(Number.self, ','), Literal.get(')'),
			new Maybe(Number.self) };

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		AstNode interactome = params.get(0);
		if (interactome.isInteractome()) {
			AstList numbers = (AstList) params.get(1);
			Set<Long> accessions = new HashSet<Long>();
			for (AstNode node : numbers) {
				AstInteger number = (AstInteger) node;
				accessions.add(number.getInt());
			}
			AstInteger radiusnode = (AstInteger) params.get(2);
			int radius = Integer.MAX_VALUE;
			if (radiusnode != null) {
				radius = (int) radiusnode.getInt();
			}
			return new AstProximity(interactome, radius, accessions);

		} else {
			return null;
		}
	}

	public int getPrecedence() {
		return Parser.PREC_UNARY_MANGLE;
	}

	public boolean isMatchingOperator(char c) {
		return c == ':';
	}

	public boolean isPrefixed() {
		return false;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("Find genes nearby: A : near (gi1, gi2, ...) [radius]");
	}

	public Token[] tasks() {
		return tokens;
	}

}
