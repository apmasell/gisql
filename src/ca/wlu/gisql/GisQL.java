package ca.wlu.gisql;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

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

import ca.wlu.gisql.ast.util.BuiltInResolver;
import ca.wlu.gisql.db.DatabaseEnvironment;
import ca.wlu.gisql.db.DatabaseManager;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.interactome.output.FileFormat;
import ca.wlu.gisql.parser.ExpressionKnowledgebase;
import ca.wlu.gisql.runner.ExpressionRunner;

/**
 * Main entry point of command line interface. All command line processing
 * should be done here.
 */
public class GisQL {

	private static class Prompt {
		public final String key;
		public final String prompt;
		public final boolean show;

		public Prompt(String key, String prompt, boolean show) {
			super();
			this.key = key;
			this.prompt = prompt;
			this.show = show;
		}

	}

	public static final boolean debug = System.getProperty("gisql.debug",
			"false").equals("true");

	private static final String HistoryFilename = ".gisql_history";

	private static final Logger log = Logger.getLogger(GisQL.class);

	private final static Prompt[] prompts = new Prompt[] {
			new Prompt("driver", "JDBC Driver", true),
			new Prompt("url", "JDBC URL", true),
			new Prompt("user", "Username", true),
			new Prompt("password", "Password", false) };

	private static final String StartFilename = ".gisqlrc";

	public static File getUserHome() {
		String userHome = System.getProperty("user.home");
		if (userHome == null) {
			throw new IllegalStateException("user.home is null");
		}
		return new File(userHome);
	}

	public static boolean isValidIdentifierPart(char c) {
		return Character.isJavaIdentifierPart(c) && c != '$';
	}

	public static boolean isValidIdentifierStart(char c) {
		return Character.isJavaIdentifierStart(c) && c != '$';
	}

	public static boolean isValidName(String name) {
		if (name.length() > 0 && isValidIdentifierStart(name.charAt(0))) {
			for (int index = 1; index < name.length(); index++) {
				if (!isValidIdentifierPart(name.charAt(index))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

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
			System.out.println(new ExpressionKnowledgebase().getHelp());
			return;
		}

		ConsoleReader reader = new ConsoleReader();
		reader.setBellEnabled(true);

		Properties configuration = DatabaseManager.getPropertiesFromFile();
		DatabaseManager dm = null;
		if (commandline.hasOption('l') || configuration == null) {
			dm = GisQL.promptForConnection(new Properties(), reader);
		} else {
			Properties properties = new Properties();

			String prefix = commandline.getOptionValue('x');
			if (prefix == null || prefix.length() == 0) {
				prefix = "";
			} else {
				prefix += '.';
			}
			boolean prompt = false;
			for (String key : new String[] { "driver", "url", "user",
					"password" }) {
				String value = configuration.getProperty(prefix + key);
				prompt |= value == null;
				properties.setProperty(key, value);
			}

			if (prompt) {
				dm = promptForConnection(properties, reader);
			} else {
				dm = new DatabaseManager(properties);
			}
		}

		if (dm == null) {
			log.error("Gave up connecting to database.");
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

		File rcfile = new File(getUserHome(), StartFilename);
		if (rcfile.exists()) {
			runner.run(rcfile, null);
		}

		if (commandline.getArgs().length > 0) {
			for (String argument : commandline.getArgs()) {
				if (!runner.run(argument, null)) {
					return;
				}
			}
			return;
		}

		if (commandline.hasOption('c')) {
			runner.run(new File(commandline.getOptionValue('c')), null);
			return;
		} else {
			reader.setDefaultPrompt("gisql> ");
			reader.addCompletor(new ArgumentCompletor(new EnvironmentCompletor(
					environment), new NonIdentifierArgumentDelimiter()));
			reader.getHistory().setHistoryFile(
					new File(getUserHome(), HistoryFilename));

			String line;
			while ((line = reader.readLine()) != null) {
				if (line.equalsIgnoreCase("quit")
						|| line.equalsIgnoreCase("exit")) {
					break;
				} else if (line.equalsIgnoreCase("help")) {
					System.out.println(environment.getParserKb().getHelp());
					System.out.println(BuiltInResolver.getHelp());
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

		Option login = new Option("l", "login", false,
				"Prompt for database login.");

		Option file = new Option("c", "command", true, "Run queries in file.");
		file.setArgName("file");

		Option output = new Option("o", "output", true,
				"Output results to file.");
		output.setArgName("file");

		Option format = new Option("F", "format", true, "Output result format.");
		format.setArgName("layout");

		Option context = new Option("x", "context", true,
				"Open a specific context.");
		context.setArgName("context");

		options.addOption(help);
		options.addOption(login);
		options.addOption(file);
		options.addOption(output);
		options.addOption(format);
		options.addOption(context);
		return options;
	}

	private static DatabaseManager promptForConnection(Properties properties,
			ConsoleReader reader) throws IOException {

		StringBuffer sb = new StringBuffer();
		while (true) {
			for (Prompt prompt : prompts) {
				sb.setLength(0);
				sb.append(prompt.prompt).append(": ");
				boolean defined = properties.containsKey(prompt.key)
						&& properties.getProperty(prompt.key).length() > 0;
				if (prompt.show && defined) {
					sb.append("[").append(properties.getProperty(prompt.key))
							.append("] ");
				}
				String value;
				do {
					value = prompt.show ? reader.readLine(sb.toString())
							: reader.readLine(sb.toString(), '*');
				} while ((value == null || value.length() == 0) && !defined);

				if (value != null && value.length() > 0) {
					properties.setProperty(prompt.key, value);
				}
			}

			try {
				return new DatabaseManager(properties);
			} catch (SQLException e) {
				log.warn("Failed to connect to database.", e);
			} catch (ClassNotFoundException e) {
				log.warn("Unknown driver.", e);
			}
			reader.printString("Try again? (y/n)");
			reader.flushConsole();
			char result = (char) reader.readCharacter(new char[] { 'y', 'Y',
					'n', 'N' });
			reader.printNewline();
			if (result != 'Y' && result != 'y') {
				return null;
			}

		}
	}
}
