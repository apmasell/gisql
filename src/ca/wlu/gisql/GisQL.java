package ca.wlu.gisql;

import ca.wlu.gisql.gui.MainFrame;
import java.sql.SQLException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interation.Interaction;

public class GisQL {

	static final Logger log = Logger.getLogger(GisQL.class);

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

		final Environment env = new Environment(dm);

		if (args.length == 1) {
			Interactome interactome = env.parse(args[0]);
			if (interactome == null) {
				log.error("Failed to parse query.");
				return;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("# query: ");
			log.info(interactome.show(sb));

			long start = System.currentTimeMillis();
			int count = 0;
			for (Interaction i : interactome) {
				sb.setLength(0);
				i.show(sb);
				System.out.println(sb);
				count++;
			}
			sb.setLength(0);
			sb.append(count);
			sb.append(" interactions in ");
			sb.append((System.currentTimeMillis() - start) / 1000.0);
			sb.append(" seconds.");
			log.info(sb);
		} else {

			java.awt.EventQueue.invokeLater(new Runnable() {

				public void run() {
					new MainFrame(env).setVisible(true);
				}
			});
		}
	}
}
