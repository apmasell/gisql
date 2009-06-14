package ca.wlu.gisql.environment.parser;

import java.util.List;

public abstract class Token {
	abstract boolean parse(int level, List<Object> results);
}