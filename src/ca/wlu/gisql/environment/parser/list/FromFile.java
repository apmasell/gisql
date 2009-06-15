package ca.wlu.gisql.environment.parser.list;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.QuotedString;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;

public class FromFile implements ListParseable {
	private static final Logger log = Logger.getLogger(FromFile.class);

	public boolean construct(Environment environment, List<AstNode> params,
			Stack<String> error, List<AstNode> results) {
		String filename = ((AstString) params.get(0)).getString();
		try {
			BufferedReader input = new BufferedReader(new FileReader(filename));
			AstList list = new AstList();
			String line;
			int linenumber = 0;
			while ((line = input.readLine()) != null) {
				linenumber++;
				Parser parser = new Parser(environment, line);
				AstNode node = parser.getRaw();
				if (node == null) {
					error.push("Script failed on line :" + linenumber);
					error.push(parser.getErrors());
					return false;
				}
				list.add(node);
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

	public Token[] tasks(Parser parser) {
		return new Token[] { new QuotedString(parser) };
	}

}
