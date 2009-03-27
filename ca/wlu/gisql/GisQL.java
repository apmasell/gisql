package ca.wlu.gisql;

import java.sql.SQLException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import ca.wlu.gisql.interactome.Database;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interation.Interaction;

public class GisQL {
	static final Logger log = Logger.getLogger(Database.class);

	public static void main(String[] args) throws Exception {
		ConsoleAppender appender = new ConsoleAppender(new PatternLayout());
		Logger.getRootLogger().addAppender(appender);

		DatabaseManager dm;

		try {
			dm = new DatabaseManager();
		} catch (SQLException e) {
			log.error("Failed to connect to database.", e);
			return;
		}

		Environment env = new Environment(dm);
		/*
		 * 25 | S_boydii_Sb227 26 | S_dysenteriae 27 | S_flexneri_2a 28 |
		 * S_flexneri_2a_2457T 29 | S_flexneri_5_8401
		 */
		Interactome curr = env
				.parse("E_coli_HS | S_flexneri_5_8401 & S_flexneri_2a_2457T");

		System.out.println(curr.show(new StringBuilder()));

		long start = System.currentTimeMillis();
		int count = 0;
		StringBuilder sb = new StringBuilder();
		for (Interaction i : curr) {
			sb.setLength(0);
			i.show(sb);
			System.out.println(sb);
			count++;
		}
		System.out.println();
		System.out.print(count);
		System.out.println(" interactions.");
		System.out.print(System.currentTimeMillis() - start);
		System.out.println(" elapsed.");

	}

}
