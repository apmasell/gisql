package ca.wlu.gisql.ast.type;

import java.util.List;
import java.util.Map;

import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.parser.descriptors.type.TypeNesting;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** The nullable type. */
public class MaybeType extends Type {
	protected final Type contents;

	public MaybeType(Type contents) {
		super();
		if (contents == null) {
			throw new IllegalArgumentException();
		}
		this.contents = contents.isNullable()
				|| contents.isOptionallyNullable() ? contents.getContents()
				: contents;

	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof MaybeType) {
			MaybeType that = (MaybeType) obj;
			return contents.equals(that.contents);
		} else {
			return false;
		}
	}

	@Override
	protected Type freshen(Map<Type, Type> replacement) {
		Type freshContents = contents.freshen(replacement);
		if (freshContents == contents) {
			return this;
		}
		return new MaybeType(freshContents);

	}

	@Override
	public Type getContents() {
		return contents;
	}

	@Override
	public TypeNesting getPrecedence() {
		return TypeNesting.Receptacle;
	}

	@Override
	public Class<?> getRootJavaType() {
		return contents.getRootJavaType();
	}

	@Override
	public Type getTerminalMaybe() {
		return this;
	}

	@Override
	public int hashCode() {
		return contents.hashCode() * 617;
	}

	@Override
	public boolean isInformational() {
		return contents.isInformational();
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	protected boolean makeNull(Type contents) {
		return this.contents.unify(contents);
	}

	@Override
	protected boolean occurs(Type needle) {
		return contents.occurs(needle);
	}

	@Override
	public <T> boolean render(Rendering<T> rendering, int depth) {
		return rendering.hP(contents)
				&& rendering.pRg$hO_CreateObject(MaybeType.class
						.getConstructors()[0]);
	}

	@Override
	public void reset() {
		contents.reset();
	}

	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print('¿');
		print.print(contents);
		print.print('?');
	}

	@Override
	public boolean unify(Type that) {
		if (this == that) {
			return true;
		} else if (that.isNullable() || that.isOptionallyNullable()) {
			return that.makeNull(contents);
		} else {
			return super.unify(that);
		}
	}

	@Override
	public boolean validate(Object value) {
		if (value == null) {
			return true;
		} else {
			return contents.validate(value);
		}
	}
}