package ca.wlu.gisql;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileReader;
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
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ToFile;
import ca.wlu.gisql.interactome.ToFile.FileFormat;

public class GisQL {

    private static FileFormat format = FileFormat.interactome;

    static final Logger log = Logger.getLogger(GisQL.class);

    public static void main(String[] args) throws Exception {
	ConsoleAppender appender = new ConsoleAppender(new PatternLayout());
	Logger.getRootLogger().addAppender(appender);

	CommandLine commandline = processCommandLine(args);

	DatabaseManager dm;
	try {
	    dm = new DatabaseManager();
	} catch (SQLException e) {
	    log.error("Failed to connect to database.", e);
	    return;
	}

	final Environment environment = new Environment(dm);

	PrintStream output = System.out;
	if (commandline.hasOption('o')) {
	    output = new PrintStream(commandline.getOptionValue('o'));
	}
	for (String argument : commandline.getArgs()) {
	    runExpression(environment, output, argument);
	}

	if (commandline.hasOption('c')) {
	    BufferedReader input = new BufferedReader(new FileReader(
		    commandline.getOptionValue('c')));
	    String line;
	    while ((line = input.readLine()) != null) {
		runExpression(environment, output, line);
	    }
	    input.close();
	}

	if (commandline.hasOption('o')) {
	    output.close();
	}

	if (commandline.hasOption('g') || commandline.getArgs().length == 0) {
	    EventQueue.invokeLater(new Runnable() {
		public void run() {
		    new MainFrame(environment).setVisible(true);
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
		GisQL.format = FileFormat.valueOf(command.getOptionValue('F'));
		if (GisQL.format == null) {
		    GisQL.format = FileFormat.interactome;
		}
	    }
	    return command;
	} catch (ParseException e) {
	    log.error("Parsing failed.", e);
	}
	return null;
    }

    private static boolean runExpression(Environment environment,
	    PrintStream output, String expression) {
	Parser parser = new Parser(environment, expression);
	Interactome interactome = parser.get();
	environment.append(interactome);
	if (interactome == null) {
	    log.error(parser.getErrors());
	    return false;
	}
	ToFile.write(interactome, format, output, 0, 1);
	return true;

    }
}
