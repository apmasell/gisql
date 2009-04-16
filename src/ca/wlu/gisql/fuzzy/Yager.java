package ca.wlu.gisql.fuzzy;

public class Yager implements TriangularNorm {

	private double p;

	public Yager(double p) {
		super();
		if (p <= 0)
			throw new IllegalArgumentException("p > 0");
		this.p = p;
	}

	public String getName() {
		return "Yager (" + p + ")";
	}

	public double s(double a, double b) {
		return Math.min(1, Math.pow(Math.pow(a, p) + Math.pow(b, p), 1 / p));
	}

	public double t(double a, double b) {
		return Math.max(0, Math.pow(Math.pow(1 - a, p) + Math.pow(1 - b, p),
				1 / p));
	}

	public double v(double x) {
		return 1 - x;
	}

}
