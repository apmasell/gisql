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
	public boolean canUnify(Object obj) {
		return obj instanceof ListType ? contents
				.canUnify(((ListType) obj).contents) : super.canUnify(obj);
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
		} else if (that instanceof ListType) {
			ListType other = (ListType) that;
			return contents.unify(other.contents);
		} else {
			return super.unify(that);
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