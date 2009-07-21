package ca.wlu.gisql.functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.wlu.gisql.GisQL;

public class ScoringMatrix<E> {

	private final Map<E, Map<E, Double>> matrix = new HashMap<E, Map<E, Double>>();

	private double minimum = Double.POSITIVE_INFINITY;
	private E minimumLeft = null;

	private E minimumRight = null;

	public ScoringMatrix(List<E> list, ScoringFunction<E> function,
			ScoringMatrix<E> old) {
		int size = list.size();

		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				E left = list.get(i);
				E right = list.get(j);

				double value = (old == null ? GisQL.Undefined : old.get(left,
						right));

				if (GisQL.isMissing(value)) {
					value = function.computeScore(left, right);
					System.gc();
				}

				if (value < minimum) {
					minimum = value;
					minimumLeft = left;
					minimumRight = right;
				}

				put(left, right, value);
			}
		}
	}

	public double get(E key, E subkey) {
		if (key.hashCode() > subkey.hashCode()) {
			E temp = key;
			key = subkey;
			subkey = temp;
		}

		Map<E, Double> submap = matrix.get(key);
		if (submap == null) {
			return GisQL.Undefined;
		}
		Double value = submap.get(subkey);
		return (value == null ? GisQL.Undefined : value);
	}

	public double getMinimum() {
		return minimum;
	}

	public E getMinimumLeft() {
		return minimumLeft;
	}

	public E getMinimumRight() {
		return minimumRight;
	}

	private void put(E key, E subkey, double value) {
		if (key.hashCode() > subkey.hashCode()) {
			E temp = key;
			key = subkey;
			subkey = temp;
		}

		Map<E, Double> submap = matrix.get(key);
		if (submap == null) {
			submap = new HashMap<E, Double>();
			matrix.put(key, submap);
		}
		submap.put(subkey, value);
	}
}
