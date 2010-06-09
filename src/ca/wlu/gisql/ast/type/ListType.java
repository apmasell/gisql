package ca.wlu.gisql.ast.type;

import java.util.List;
import java.util.Map;

import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** The query language type of a list, synonymous with Java's List. */
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
		if (obj instanceof ListType) {
			ListType that = (ListType) obj;
			return contents.equals(that.contents);
		}
		return false;
	}

	@Override
	protected Type freshen(Map<Type, Type> replacement) {
		Type freshContents = contents.freshen(replacement);
		if (freshContents == contents) {
			return this;
		}
		return new ListType(freshContents);

	}

	@Override
	public Type getContents() {
		return contents;
	}

	@Override
	public Class<?> getRootJavaType() {
		return List.class;
	}

	@Override
	public int hashCode() {
		return contents.hashCode() * 19;
	}

	@Override
	protected boolean occurs(Type needle) {
		return contents.occurs(needle);
	}

	@Override
	public <T> boolean render(Rendering<T> rendering, int depth) {
		return rendering.hP(contents)
				&& rendering.pRg$hO_CreateObject(ListType.class
						.getConstructors()[0]);
	}

	@Override
	public void reset() {
		contents.reset();
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
		if (value instanceof List<?>) {
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