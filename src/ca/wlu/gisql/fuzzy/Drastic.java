package ca.wlu.gisql.fuzzy;

public class Drastic implements TriangularNorm {

    public String getName() {
	return "Drastic";
    }

    public double s(double a, double b) {
	return (Math.min(a, b) == 0 ? Math.max(a, b) : 1);
    }

    public double t(double a, double b) {
	return (Math.max(a, b) == 1 ? Math.min(a, b) : 0);
    }

    public double v(double x) {
	return 1 - x;
    }

}
