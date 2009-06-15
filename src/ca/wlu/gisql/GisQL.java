package ca.wlu.gisql;

import java.awt.EventQueue;
import java.io.File;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import ca.wlu.gisql.db.DatabaseEnvironment;
import ca.wlu.gisql.db.DatabaseManager;
import ca.wlu.gisql.environment.EnvironmentUtils;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.gui.MainFrame;
import ca.wlu.gisql.interactome.output.FileFormat;

public class GisQL {

	private static UserEnvironment environment;

	private static final Logger log = Logger.getLogger(GisQL.class);

	public static final double Missing = -1;

	public static boolean isMissing(double membership) {
		return membership < 0;
	}

	public static void main(String[] args) throws Exception {
		ConsoleAppender appender = new ConsoleAppender(new PatternLayout());
		Logger.getRootLogger().addAppender(appender);

		DatabaseManager dm;
		try {
			dm = new DatabaseManager(DatabaseManager.getPropertiesFromFile());
		} catch (SQLException e) {
			log.error("Failed to connect to database.", e);
			return;
		}

		environment = new UserEnvironment(new DatabaseEnvironment(dm));

		Options options = makeOptions();
		CommandLine commandline = processCommandLine(options, args);

		if (commandline.hasOption('h')) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("gisql", options);
			System.out.println(Parser.getHelp());
			return;
		}

		if (commandline.hasOption('o')) {
			environment.setOutput(commandline.getOptionValue('o'));
		}
		for (String argument : commandline.getArgs()) {
			boolean success = EnvironmentUtils.runExpression(environment,
					argument, true);
			if (!success)
				return;
		}

		if (commandline.hasOption('c')) {
			EnvironmentUtils.runFile(environment, new File(commandline
					.getOptionValue('c')));
		}

		if (commandline.hasOption('o')) {
			environment.setOutput(null);
		}

		if (commandline.hasOption('g')
				|| (commandline.getArgs().length == 0 && !commandline
						.hasOption('c'))) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					new MainFrame(GisQL.environment).setVisible(true);
				}
			});
		}
	}

	private static Options makeOptions() {
		Options options = new Options();

		Option help = new Option("h", "help", false, "Display this non-sense.");

		Option file = new Option("c", "command", true, "Run queries in file.");
		file.setArgName("file");

		Option output = new Option("o", "output", true,
				"Output results to file.");
		output.setArgName("file");

		Option format = new Option("F", "format", true, "Output result format.");
		format.setArgName("layout");

		Option gui = new Option("g", "gui", false, "Use graphical interface.");

		options.addOption(help);
		options.addOption(file);
		options.addOption(output);
		options.addOption(format);
		options.addOption(gui);
		return options;
	}

	private static CommandLine processCommandLine(Options options, String[] args) {
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
