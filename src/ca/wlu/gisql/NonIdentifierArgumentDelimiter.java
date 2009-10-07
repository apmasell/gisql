package ca.wlu.gisql;

import jline.ArgumentCompletor;

public class NonIdentifierArgumentDelimiter extends
		ArgumentCompletor.AbstractArgumentDelimiter {
	@Override
	public boolean isDelimiterChar(String buffer, int pos) {
		char c = buffer.charAt(pos);
		return !(Character.isJavaIdentifierStart(c) || Character
				.isJavaIdentifierPart(c));
	}
}
