package ca.wlu.gisql.fuzzy;

public class Schweizer implements TriangularNorm {

	private double p;

	public Schweizer(double p) {
		super();
		if (p <= 0)
			throw new IllegalArgumentException("p > 0");
		this.p = p;
	}

	public String getName() {
		return "Schweizer (" + p + ")";
	}

	public double s(double a, double b) {
		return 1 - Math.pow(Math
				.max(0, Math.pow(1 - a, p) + Math.pow(1 - b, p)), 1 / p);
	}

	public double t(double a, double b) {
		return Math
				.pow(Math.max(0, Math.pow(a, p) + Math.pow(b, p) - 1), 1 / p);
	}

	public double v(double x) {
		return 1 - x;
	}

}
