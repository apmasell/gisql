package ca.wlu.gisql.ast.type;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.parser.descriptors.type.TypeNesting;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** The query language type of a list, synonymous with Java's List. */
public class PairType extends Type {
	private final Type left;
	private final Type right;

	public PairType(Type left, Type right) {
		super();
		if (left == null || right == null) {
			throw new IllegalArgumentException(
					"Pair types cannot include null.");
		}
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PairType) {
			PairType that = (PairType) obj;
			return left.equals(that.left) && right.equals(right);
		}
		return false;
	}

	@Override
	protected Type freshen(Map<Type, Type> replacement) {
		Type freshleft = left.freshen(replacement);
		Type freshright = right.freshen(replacement);
		if (freshleft == left && freshright == right) {
			return this;
		}
		return new PairType(freshleft, freshright);

	}

	@Override
	public TypeNesting getPrecedence() {
		return TypeNesting.Couple;
	}

	@Override
	public Class<?> getRootJavaType() {
		return Entry.class;
	}

	@Override
	public int hashCode() {
		return left.hashCode() * 3001 + right.hashCode() * 2017;
	}

	@Override
	protected boolean occurs(Type needle) {
		return left.occurs(needle) || right.occurs(needle);
	}

	@Override
	public <T> boolean render(Rendering<T> rendering, int depth) {
		return rendering.hP(right)
				&& rendering.hP(left)
				&& rendering.pRg$hO_CreateObject(PairType.class
						.getConstructors()[0]);
	}

	@Override
	public void reset() {
		left.reset();
		right.reset();
	}

	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		print.print(left, TypeNesting.Receptacle);
		print.print("*");
		print.print(right, TypeNesting.Couple);
	}

	@Override
	public boolean unify(Type that) {
		if (this == that) {
			return true;
		} else if (that instanceof PairType) {
			PairType other = (PairType) that;
			return left.unify(other.left) && right.unify(other.right);
		} else {
			return super.unify(that);
		}
	}

	@Override
	public boolean validate(Object value) {
		if (value instanceof Entry<?, ?>) {
			Entry<?, ?> entry = (Entry<?, ?>) value;
			return left.validate(entry.getKey())
					&& right.validate(entry.getValue());
		} else {
			return false;
		}
	}
}