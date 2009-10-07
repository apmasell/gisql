package ca.wlu.gisql;

import java.io.File;
import java.sql.SQLException;

import jline.ArgumentCompletor;
import jline.ConsoleReader;

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
import ca.wlu.gisql.environment.ParserEnvironment;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.interactome.output.FileFormat;
import ca.wlu.gisql.runner.ExpressionRunner;

public class GisQL {

	private static final Logger log = Logger.getLogger(GisQL.class);

	public static void main(String[] args) throws Exception {
		ConsoleAppender appender = new ConsoleAppender(new PatternLayout());
		Logger.getRootLogger().addAppender(appender);

		Options options = makeOptions();
		CommandLine commandline;

		try {
			commandline = new GnuParser().parse(options, args);
		} catch (ParseException e) {
			log.error("Parsing failed.", e);
			return;
		}

		if (commandline.hasOption('h')) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("gisql", options);
			System.out.println(ParserEnvironment.self.getParserKb().getHelp());
			return;
		}

		DatabaseManager dm;
		try {
			dm = new DatabaseManager(DatabaseManager.getPropertiesFromFile());
		} catch (SQLException e) {
			log.error("Failed to connect to database.", e);
			return;
		}

		UserEnvironment environment = new UserEnvironment(
				new DatabaseEnvironment(dm));

		if (commandline.hasOption('o')) {
			environment.setOutput(commandline.getOptionValue('o'));
		}
		if (commandline.hasOption('F')) {
			FileFormat fileformat = FileFormat.valueOf(commandline
					.getOptionValue('F'));
			if (fileformat == null) {
				fileformat = FileFormat.interactome;
			}
			environment.setFormat(fileformat);
		}

		ConsoleRunListener listener = new ConsoleRunListener(environment);
		ExpressionRunner runner = new ExpressionRunner(environment, listener);

		for (String argument : commandline.getArgs()) {
			if (!runner.run(argument, null)) {
				return;
			}
			return;
		}

		if (commandline.hasOption('c')) {
			runner.run(new File(commandline.getOptionValue('c')), null);
			return;
		} else {
			ConsoleReader reader = new ConsoleReader();
			reader.setBellEnabled(true);
			reader.setDefaultPrompt("gisql> ");
			reader.addCompletor(new ArgumentCompletor(new EnvironmentCompletor(
					environment), new NonIdentifierArgumentDelimiter()));

			String line;
			while ((line = reader.readLine()) != null) {
				if (line.equalsIgnoreCase("quit")
						|| line.equalsIgnoreCase("exit")) {
					break;
				} else if (line.equalsIgnoreCase("help")) {
					System.out.println(ParserEnvironment.self.getParserKb()
							.getHelp());
				} else if (line.trim().length() > 0) {
					runner.run(line, null);
				}
			}
			System.out.println();
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

		options.addOption(help);
		options.addOption(file);
		options.addOption(output);
		options.addOption(format);
		return options;
	}
}
