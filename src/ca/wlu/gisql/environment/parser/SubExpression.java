package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.interactome.Interactome;

public class SubExpression extends Token {

	private final Parser parser;

	public SubExpression(Parser parser) {
		this.parser = parser;
	}

	boolean parse(int level, List<Object> results) {
		Interactome result = (level == Parser.maxdepth ? this.parser
				.parseIdentifier() : this.parser.parseAutoExpression(level + 1));
		if (result == null)
			return false;
		results.add(result);
		return true;
	}
}