package ca.wlu.gisql.environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.output.AbstractOutput;

public class EnvironmentUtils {
	static final Logger log = Logger.getLogger(EnvironmentUtils.class);

	public static boolean runExpression(UserEnvironment environment,
			String expression, boolean append) {
		Parser parser = new Parser(environment, expression);
		switch (parser.getParseResult()) {
		case Interactome:
			CachedInteractome interactome = AbstractOutput.wrap(parser.get(),
					null, 0.0, 1.0, environment.getFormat(), environment
							.getOutput(), false);
			if (interactome == null) {
				log.error(parser.getErrors());
				return false;
			}
			if (interactome.process()) {
				if (append)
					environment.append(interactome);
				return true;
			} else {
				return false;
			}
		case Executable:
			parser.execute();
			return true;
		case Failure:
			return false;
		}
		return false;
	}

	public static boolean runFile(UserEnvironment environment, File file) {
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line;
			int linenumber = 0;
			while ((line = input.readLine()) != null) {
				linenumber++;
				if (!runExpression(environment, line, false)) {
					log.error("Script failed on line :" + linenumber);
					return false;
				}
			}
			input.close();
			return true;
		} catch (IOException e) {
			log.error("Script error.", e);
		}
		return true;
	}

}
