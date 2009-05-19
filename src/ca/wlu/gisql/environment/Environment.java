package ca.wlu.gisql.environment;

import java.util.List;

import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Type;

public interface Environment {
	public interface EnvironmentListener {
		public abstract void addedEnvironmentVariable(String name,
				Interactome interactome);

		public abstract void droppedEnvironmentVariable(String name,
				Interactome interactome);

		public abstract void lastChanged();
	}

	public abstract void addListener(EnvironmentListener listener);

	public abstract Interactome getLast();

	public abstract TriangularNorm getTriangularNorm();

	public abstract Interactome getVariable(String name);

	public abstract List<String> names(Type filter);

	public abstract List<String> names(Type filter, List<String> list);

	public abstract void removeListener(EnvironmentListener listener);

	public abstract boolean setTriangularNorm(TriangularNorm norm);

	public abstract boolean setVariable(String name, Interactome interactome);

	public abstract List<Interactome> variables(Type filter);

	public abstract List<Interactome> variables(Type filter,
			List<Interactome> list);
}
