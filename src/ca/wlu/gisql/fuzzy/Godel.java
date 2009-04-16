package ca.wlu.gisql.fuzzy;

public class Godel implements TriangularNorm {

	public String getName() {
		return "Gödel";
	}

	public double s(double a, double b) {
		return Math.max(a, b);
	}

	public double t(double a, double b) {
		return Math.min(a, b);
	}

	public double v(double x) {
		return 1 - x;
	}

}
