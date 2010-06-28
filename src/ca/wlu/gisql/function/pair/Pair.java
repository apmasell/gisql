package ca.wlu.gisql.function.pair;

import java.util.Map.Entry;

import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.annotation.GisqlType;

@GisqlConstructorFunction(name = "pair", description = "Construct a new pair from two values.")
public class Pair<K, V> implements Entry<K, V> {
	private final K key;
	private final V value;

	@GisqlType(type = "α*β")
	public Pair(@GisqlType(type = "α") K key, @GisqlType(type = "β") V value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		return null;
	}

	@Override
	public String toString() {
		return key.toString() + "*" + value.toString();
	}
}
