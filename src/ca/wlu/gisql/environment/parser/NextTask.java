package ca.wlu.gisql.environment.parser;

import java.util.List;

public abstract class NextTask {
	abstract boolean parse(int level, List<Object> results);
}