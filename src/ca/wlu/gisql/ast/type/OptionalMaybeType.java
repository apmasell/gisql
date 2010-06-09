package ca.wlu.gisql.ast.type;

import java.util.List;
import java.util.Map;

import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class OptionalMaybeType extends MaybeType {

	public static Type wrap(Type type) {
		if (type.isNullable()) {
			return type;
		} else {
			return new OptionalMaybeType(type);
		}
	}

	boolean lifted;

	public OptionalMaybeType(Type contents) {
		super(contents);
		lifted = contents.isNullable();
	}

	@Override
	protected Type freshen(Map<Type, Type> replacement) {
		if (lifted) {
			return super.freshen(replacement);
		} else {
			return new OptionalMaybeType(contents.freshen(replacement));
		}
	}

	@Override
	public Type getTerminalMaybe() {
		lifted = true;
		return super.getTerminalMaybe();
	}

	@Override
	public boolean isNullable() {
		return lifted;
	}

	@Override
	public boolean isOptionallyNullable() {
		return true;
	}

	public void lift() {
		lifted = true;
	}

	@Override
	protected boolean makeNull(Type contents) {
		lifted = true;
		return this.contents.unify(contents);
	}

	@Override
	public <T> boolean render(Rendering<T> rendering, int depth) {
		if (lifted) {
			return super.render(rendering, depth);
		} else {
			return contents.render(rendering, depth);
		}
	}

	@Override
	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		if (lifted) {
			super.show(print);
		} else {
			print.print(contents);
		}
	}

	@Override
	public boolean unify(Type that) {
		if (this == that) {
			return true;
		} else if (that.isNullable()) {
			return that.makeNull(contents) && (lifted = true);
		} else if (that.isOptionallyNullable()) {
			if (that instanceof TypeVariable) {
				return that.unify(this);
			} else {
				OptionalMaybeType other = (OptionalMaybeType) that;
				if (other.contents.unify(contents)) {
					other.lifted = lifted = other.lifted || lifted;
					return true;
				} else {
					return false;
				}
			}
		} else {
			return contents.unify(that);
		}
	}
}
