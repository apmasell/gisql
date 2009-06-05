package ca.wlu.gisql.environment.parser.list;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.NextTask;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.QuotedString;
import ca.wlu.gisql.interactome.Interactome;

public class FromFile implements ListParseable {
	private static final Logger log = Logger.getLogger(FromFile.class);

	public boolean construct(Environment environment, List<Object> params,
			Stack<String> error, List<Object> results) {
		String filename = (String) params.get(0);
		try {
			BufferedReader input = new BufferedReader(new FileReader(filename));
			List<Interactome> list = new ArrayList<Interactome>();
			String line;
			int linenumber = 0;
			while ((line = input.readLine()) != null) {
				linenumber++;
				Parser parser = new Parser(environment, line);
				Interactome interactome = parser.get();
				if (interactome == null) {
					error.push("Script failed on line :" + linenumber);
					error.push(parser.getErrors());
					return false;
				}
				list.add(interactome);
			}
			input.close();
			results.add(list);
			return true;
		} catch (IOException e) {
			error.push("I/O error reading script: " + filename);
			log.error("I/O error reading script: " + filename, e);
			return false;
		}
	}

	public PrintStream show(PrintStream print) {
		print.print("Read List from File: \"filename\"");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("Read List from File: \"filename\"");
		return sb;
	}

	public NextTask[] tasks(Parser parser) {
		return new NextTask[] { new QuotedString(parser) };
	}

}
