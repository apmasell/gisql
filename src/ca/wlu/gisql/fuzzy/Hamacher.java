package ca.wlu.gisql.fuzzy;

public class Hamacher implements TriangularNorm {
    private double p;

    public Hamacher(double p) {
	super();
	if (p < 0)
	    throw new IllegalArgumentException("p >= 0");
	this.p = p;
    }

    public String getName() {
	return "Hamacher (" + p + ")";
    }

    public double s(double a, double b) {
	return (a + b - a * b - (1 - p) * a * b) / (1 - (1 - p) * a * b);
    }

    public double t(double a, double b) {
	return a * b / (p + (1 - p) * a * b);
    }

    public double v(double x) {
	return 1 - x;
    }

}
