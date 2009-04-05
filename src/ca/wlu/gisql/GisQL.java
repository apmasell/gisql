package ca.wlu.gisql;

import java.awt.EventQueue;
import java.sql.SQLException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import ca.wlu.gisql.gui.MainFrame;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ToFile;

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

	if (args.length > 0) {
	    for (String argument : args) {
		Interactome interactome = env.parse(argument);
		if (interactome == null) {
		    log.error("Failed to parse query.");
		    return;
		}
		ToFile.write(interactome, ToFile.FORMAT_INTERACTOME_TEXT,
			System.out, 0, 1);
	    }
	} else {
	    EventQueue.invokeLater(new Runnable() {
		public void run() {
		    new MainFrame(env).setVisible(true);
		}
	    });
	}
    }
}
