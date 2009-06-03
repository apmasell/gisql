package ca.wlu.gisql.graph;

import java.io.PrintStream;

import ca.wlu.gisql.db.DbSpecies;
import ca.wlu.gisql.util.Show;

public final class Accession implements Show {
	private final long identifier;

	private final String name;

	private final DbSpecies species;

	public Accession(long identifier, DbSpecies species, String name) {
		this.identifier = identifier;
		this.species = species;
		this.name = name;
	}

	public long getIdentifier() {
		return identifier;
	}

	public String getName() {
		return name;
	}

	public DbSpecies getSpecies() {
		return species;
	}

	public PrintStream show(PrintStream print) {
		print.print(name);
		print.print(" [gi:");
		print.print(identifier);
		print.print("/");
		species.show(print);
		print.print("]");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(name);
		sb.append(" [gi:");
		sb.append(identifier);
		sb.append("/");
		species.show(sb);
		sb.append("]");
		return sb;
	}
	
	public String toString() {
		return show(new StringBuilder()).toString();
	}
}