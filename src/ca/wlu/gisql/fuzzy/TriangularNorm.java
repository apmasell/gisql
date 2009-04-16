package ca.wlu.gisql.fuzzy;

public interface TriangularNorm {
	public String getName();

	public double s(double a, double b);

	public double t(double a, double b);

	public double v(double x);
}
