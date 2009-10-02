package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;

public class ListType extends Type {
	private final Type contents;

	public ListType(Type contents) {
		super();
		if (contents == null) {
			throw new IllegalArgumentException(
					"List types cannot include null.");
		}
		this.contents = contents;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof ListType) {
			ListType other = (ListType) obj;
			return other.contents.equals(contents);
		}
		return false;
	}

	@Override
	protected Type freshen(Type needle, Type replacement) {
		Type freshContents = contents.freshen(needle, replacement);
		if (freshContents == contents) {
			return this;
		}
		return new ListType(freshContents);

	}

	@Override
	protected boolean occurs(Type needle) {
		return contents.occurs(needle);
	}

	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print("[");
		print.print(contents);
		print.print("]");
	}

	@Override
	public boolean unify(Type that) {
		if (this == that) {
			return true;
		} else if (that instanceof TypeVariable) {
			return ((TypeVariable) that).reverseUnify(this);
		} else if (that instanceof ListType) {
			ListType other = (ListType) that;
			return contents.unify(other.contents);
		} else {
			return false;
		}
	}

	@Override
	public boolean validate(Object value) {
		if (value instanceof List) {
			List<?> list = (List<?>) value;
			for (Object object : list) {
				if (!contents.validate(object)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
}