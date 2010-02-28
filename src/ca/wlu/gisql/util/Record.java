package ca.wlu.gisql.util;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class Record extends TreeMap<String, Object> implements
		Iterable<Entry<String, Object>>, Show<Object> {

	@Override
	public Iterator<Entry<String, Object>> iterator() {
		return entrySet().iterator();
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print("< ");
		boolean first = true;
		for (Entry<String, Object> entry : entrySet()) {
			if (first) {
				first = false;
			} else {
				print.print(" / ");
			}
			print.print(entry.getKey());
			print.print(" = ");
			print.print(entry.getValue());

		}
		print.print(" >");
	}

	@Override
	public final String toString() {
		return ShowableStringBuilder.toString(this, null);
	}
}
