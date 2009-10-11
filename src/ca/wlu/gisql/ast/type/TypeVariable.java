package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;

public class TypeVariable extends Type {
	private Type self = null;

	@Override
	protected Type freshen(Type needle, Type replacement) {
		if (this == needle) {
			return replacement;
		} else {

			return this;
		}
	}

	public Type getDeterminedType() {
		return self;
	}

	@Override
	protected boolean isArrow() {
		return self == null ? false : self.isArrow();
	}

	@Override
	protected boolean occurs(Type needle) {
		return this == needle;
	}

	protected boolean reverseUnify(Type that) {
		if (that.occurs(this)) {
			return false;
		} else if (self == null) {
			self = that;
			return true;
		} else {
			return that.unify(self);
		}
	}

	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		if (self == null) {
			int index = print.getContext().indexOf(this);
			if (index == -1) {
				index = print.getContext().size();
				print.getContext().add(this);
			}

			print.print((char) ('α' + index));
		} else {
			print.print(self);
		}
	}

	@Override
	public boolean unify(Type that) {
		if (this == that) {
			return true;
		} else if (that.occurs(this)) {
			return false;
		} else if (self == null) {
			self = that;
			return true;
		} else {
			return self.unify(that);
		}
	}

	@Override
	public boolean validate(Object value) {
		return self == null || self.validate(value);
	}

}