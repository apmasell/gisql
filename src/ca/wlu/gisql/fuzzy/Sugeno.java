package ca.wlu.gisql.fuzzy;

public class Sugeno implements TriangularNorm {
    private double p;

    public Sugeno(double p) {
	super();
	if (p <= -1)
	    throw new IllegalArgumentException("p > -1");
	this.p = p;
    }

    public String getName() {
	return "Sugeno (" + p + ")";
    }

    public double s(double a, double b) {
	return Math.min(1, a + b + p * a * b);
    }

    public double t(double a, double b) {
	return Math.max(0, (1 + p) * (a + b - 1) - p * a * b);
    }

    public double v(double x) {
	return 1 - x;
    }

}
