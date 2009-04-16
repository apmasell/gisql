package ca.wlu.gisql.fuzzy;

public class Mizumoto2 implements TriangularNorm {

    public String getName() {
	return "Mizumoto2";
    }

    public double s(double a, double b) {
	return 2
		/ Math.PI
		* Math.acos(Math.max(0, Math.cos(2 * a / Math.PI)
			+ Math.cos(2 * b / Math.PI) - 1));
    }

    public double t(double a, double b) {
	return 2
		/ Math.PI
		* Math.asin(Math.max(0, Math.sin(2 * a / Math.PI)
			+ Math.sin(2 * b / Math.PI) - 1));
    }

    public double v(double x) {
	return 1 - x;
    }
}
