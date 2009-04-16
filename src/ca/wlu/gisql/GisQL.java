package ca.wlu.gisql;

import java.awt.EventQueue;
import java.io.File;
import java.io.PrintStream;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import ca.wlu.gisql.gui.MainFrame;
import ca.wlu.gisql.interactome.ToFile.FileFormat;

public class GisQL {

    static Environment environment;

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

	environment = new Environment(dm);

	CommandLine commandline = processCommandLine(args);

	if (commandline.hasOption('o')) {
	    environment.setOutput(new PrintStream(commandline
		    .getOptionValue('o')));
	}
	for (String argument : commandline.getArgs()) {
	    environment.runExpression(argument);
	}

	if (commandline.hasOption('c')) {
	    environment.runFile(new File(commandline.getOptionValue('c')));
	}

	if (commandline.hasOption('o')) {
	    environment.getOutput().close();
	    environment.setOutput(System.out);
	}

	if (commandline.hasOption('g') || commandline.getArgs().length == 0) {
	    EventQueue.invokeLater(new Runnable() {
		public void run() {
		    new MainFrame(GisQL.environment).setVisible(true);
		}
	    });
	}
    }

    private static CommandLine processCommandLine(String[] args) {
	Options options = new Options();

	Option file = new Option("c", "command", true, "Run queries in file.");
	file.setArgName("file");

	Option output = new Option("o", "output", true,
		"Output results to file.");
	output.setArgName("file");

	Option format = new Option("F", "format", true, "Output result format.");
	format.setArgName("layout");

	Option gui = new Option("g", "gui", false, "Use graphical interface.");

	options.addOption(file);
	options.addOption(output);
	options.addOption(format);
	options.addOption(gui);
	try {
	    CommandLine command = new GnuParser().parse(options, args);
	    if (command.hasOption('F')) {
		FileFormat fileformat = FileFormat.valueOf(command
			.getOptionValue('F'));
		if (fileformat == null) {
		    fileformat = FileFormat.interactome;
		}
		GisQL.environment.setFormat(fileformat);
	    }
	    return command;
	} catch (ParseException e) {
	    log.error("Parsing failed.", e);
	}
	return null;
    }
}
