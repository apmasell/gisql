package ca.wlu.gisql.util;

import java.util.Map;
import java.util.Map.Entry;

public interface Record extends Iterable<Entry<String, Object>>,
		Map<String, Object>, Show<Object> {

}