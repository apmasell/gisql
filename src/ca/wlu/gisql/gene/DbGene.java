package ca.wlu.gisql.gene;

import java.io.PrintStream;
import java.util.List;

import ca.wlu.gisql.interactome.Interactome;

public class DbGene implements Gene {

	private long identifier;

	private String name;

	private Interactome parent;

	public DbGene(Interactome parent, long identifier, String name) {
		this.parent = parent;
		this.identifier = identifier;
		this.name = name;
	}

	public int countOrthologs(Interactome right) {
		return right.countOrthologs(this);
	}

	public long getId() {
		return identifier;
	}

	public double getMembership() {
		return 1;
	}

	public String getName() {
		return name;
	}

	public int getNumberOfOrthologies() {
		return 1;
	}

	public void getSupplementaryIds(List<Long> ids) {
		ids.add(identifier);
	}

	public PrintStream show(PrintStream print) {
		print.print(identifier);
		print.print(" ");
		parent.show(print);
		if (name.length() > 0) {
			print.print(" [");
			print.print(name);
			print.print("]");
		}
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(identifier).append(" ");
		parent.show(sb);
		if (name.length() > 0)
			sb.append(" [").append(name).append("]");
		return sb;
	}

	public String toString() {
		return show(new StringBuilder()).toString();
	}
}
