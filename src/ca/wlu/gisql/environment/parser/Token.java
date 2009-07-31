package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;

public abstract class Token {
	abstract boolean parse(Parser parser, int level, List<AstNode> results);
}