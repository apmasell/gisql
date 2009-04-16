package ca.wlu.gisql.fuzzy;

public class Mizumoto implements TriangularNorm {

	public String getName() {
		return "Mizumoto";
	}

	public double s(double a, double b) {
		return 2
				/ Math.PI
				* Math.asin(Math.min(1, Math.sin(2 * a / Math.PI)
						+ Math.sin(2 * b / Math.PI)));
	}

	public double t(double a, double b) {
		return 2
				/ Math.PI
				* Math.acos(Math.min(1, Math.cos(2 * a / Math.PI)
						+ Math.cos(2 * b / Math.PI)));
	}

	public double v(double x) {
		return 1 - x;
	}

}
