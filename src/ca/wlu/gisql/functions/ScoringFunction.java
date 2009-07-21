package ca.wlu.gisql.functions;

public interface ScoringFunction<T> {
	public double computeScore(T left, T right);
}
