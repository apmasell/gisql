package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstNode;

public abstract class Token {
	abstract boolean parse(Parser parser, int level, List<AstNode> results);
}