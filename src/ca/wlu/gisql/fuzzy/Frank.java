package ca.wlu.gisql.fuzzy;

public class Frank implements TriangularNorm {
	private double p;

	public Frank(double p) {
		super();
		if (p <= 0 && p == 1)
			throw new IllegalArgumentException("1 != p > 0");
		this.p = p;
	}

	public String getName() {
		return "Frank (" + p + ")";
	}

	public double s(double a, double b) {
		return 1
				- Math.log(1 + (Math.pow(p, 1 - a) - 1)
						* (Math.pow(p, 1 - b) - 1) / (p - 1)) / Math.log(p);
	}

	public double t(double a, double b) {
		return Math.log(1 + (Math.pow(p, a) - 1) * (Math.pow(p, b) - 1)
				/ (p - 1))
				/ Math.log(p);
	}

	public double v(double x) {
		return 1 - x;
	}

}
