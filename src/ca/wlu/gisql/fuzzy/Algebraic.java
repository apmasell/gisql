package ca.wlu.gisql.fuzzy;

public class Algebraic implements TriangularNorm {

    public String getName() {
	return "Algebraic";
    }

    public double s(double a, double b) {
	return a * b;
    }

    public double t(double a, double b) {
	return a + b - a * b;
    }

    public double v(double x) {
	return 1 - x;
    }

}
