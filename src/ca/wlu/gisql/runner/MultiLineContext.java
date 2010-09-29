package ca.wlu.gisql.runner;

import java.util.ArrayList;
import java.util.List;

public class MultiLineContext extends LineContext {

	private final List<LineContext> multilines = new ArrayList<LineContext>();

	public MultiLineContext(LineContext initial) {
		multilines.add(initial);
	}

	public void append(LineContext child) {
		multilines.add(child);
	}

	@Override
	public PositionContext getContextForPosition(int index) {
		int current = 0;
		while (current < multilines.size()
				&& index > multilines.get(current).getLine().length()) {
			index -= multilines.get(current).getLine().length();
			current++;
		}
		return new PositionContext(multilines.get(current), index);
	}

	@Override
	public String getLine() {
		return multilines.get(0).getLine();
	}

	@Override
	public String getSource() {
		return multilines.get(0).getSource();
	}

}
