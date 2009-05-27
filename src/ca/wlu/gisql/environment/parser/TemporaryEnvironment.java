package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Unit;
import ca.wlu.gisql.interactome.Interactome.Type;

public class TemporaryEnvironment extends NextTask {
	private class DebrujinInteractome extends Unit {
		private final int depth;

		public DebrujinInteractome(final int depth) {
			super();
			this.depth = depth;
		}

		public Interactome fork(Interactome substitute) {
			if (depth == 1)
				return substitute;
			else
				return new DebrujinInteractome(depth - 1);
		}

		public boolean needsFork() {
			return true;
		}

	}

	private class MaskedEnvironment implements Environment {
		private final Interactome index;

		private final Environment parent;

		public MaskedEnvironment(Environment parent) {
			this.parent = parent;
			index = new DebrujinInteractome(getDepth());
		}

		public void addListener(EnvironmentListener listener) {
			parent.addListener(listener);
		}

		public List<Interactome> getArray(String name) {
			return parent.getArray(name);
		}

		public int getDepth() {
			if (parent instanceof MaskedEnvironment) {
				return ((MaskedEnvironment) parent).getDepth() + 1;
			} else {
				return 1;
			}
		}

		public Interactome getLast() {
			return parent.getLast();
		}

		public TriangularNorm getTriangularNorm() {
			return parent.getTriangularNorm();
		}

		public Interactome getVariable(String name) {
			if (TemporaryEnvironment.this.name.getResult().equals(name))
				return index;
			return parent.getVariable(name);
		}

		public List<String> names(Type filter) {
			return parent.names(filter);
		}

		public List<String> names(Type filter, List<String> list) {
			return parent.names(filter, list);
		}

		public void removeListener(EnvironmentListener listener) {
			parent.removeListener(listener);
		}

		public boolean setArray(String name, List<Interactome> array) {
			return parent.setArray(name, array);
		}

		public boolean setTriangularNorm(TriangularNorm norm) {
			return parent.setTriangularNorm(norm);
		}

		public boolean setVariable(String name, Interactome interactome) {
			return parent.setVariable(name, interactome);
		}

		public List<Interactome> variables(Type filter) {
			return parent.variables(filter);
		}

		public List<Interactome> variables(Type filter, List<Interactome> list) {
			return parent.variables(filter, list);
		}

	}

	private final NextTask expression;

	private final Name name;

	private final Parser parser;

	public TemporaryEnvironment(Parser parser, Name name, NextTask expression) {
		this.parser = parser;
		this.name = name;
		this.expression = expression;
	}

	boolean parse(int level, List<Object> results) {
		Environment oldEnvironment = parser.environment;
		parser.environment = new MaskedEnvironment(oldEnvironment);
		boolean result;

		result = expression.parse(level, results);

		parser.environment = oldEnvironment;
		return result;
	}

}