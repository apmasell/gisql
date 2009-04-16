package ca.wlu.gisql.fuzzy;

public class Lukasiewicz implements TriangularNorm {

    public String getName() {
	return "Łukasiewicz";
    }

    public double s(double a, double b) {
	return Math.min(1, a + b);
    }

    public double t(double a, double b) {
	return Math.max(0, a + b - 1);
    }

    public double v(double x) {
	return 1 - x;
    }

}
