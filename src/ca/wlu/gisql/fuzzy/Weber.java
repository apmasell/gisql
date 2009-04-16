package ca.wlu.gisql.fuzzy;

public class Weber implements TriangularNorm {
    private double p;

    public Weber(double p) {
	super();
	if (p <= -1)
	    throw new IllegalArgumentException("p > -1");
	this.p = p;
    }

    public String getName() {
	return "Weber (" + p + ")";
    }

    public double s(double a, double b) {
	return Math.min(1, ((1 + p) * (a * b) - p * a * b) / (1 + p));
    }

    public double t(double a, double b) {
	return Math.max(0, (a + b - 1 + p * a * b) / (1 + p));
    }

    public double v(double x) {
	return 1 - x;
    }

}
