package ca.wlu.gisql.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstSequence;
import ca.wlu.gisql.ast.type.MaybeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.BuiltInResolver;
import ca.wlu.gisql.ast.util.EnvironmentResolver;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.util.ShowableStringBuilder;

/** Utility class to make executing query language programs simple. */
public class ExpressionRunner {
	public static final ExpressionRunner empty = new ExpressionRunner(null,
			null);

	private static final Logger log = Logger.getLogger(ExpressionRunner.class);

	private final UserEnvironment environment;

	private final List<ExpressionError> errors = new ArrayList<ExpressionError>();

	private final ExpressionRunListener listener;

	/**
	 * Creates a new runner.
	 * 
	 * @param environment
	 *            The environment in which the expression will be run.
	 * @param listener
	 *            An object that will get the results and errors created by
	 *            execution.
	 */
	public ExpressionRunner(UserEnvironment environment,
			ExpressionRunListener listener) {
		super();
		this.environment = environment;
		this.listener = listener;
	}

	public void appendBadTypeError(Type type, Type desired, AstNode node,
			ExpressionContext context) {
		appendTypeError(" Probably a bug!", type, desired, node, context);
	}

	/** Used by {@link AstNode}s during resolution phase to note any errors. */
	public void appendResolutionError(String message, AstNode node,
			ExpressionContext context) {
		errors.add(new ExpressionError(context.getAstContext(node), message,
				null));
	}

	private void appendTypeError(String message, Type type, Type desired,
			AstNode node, ExpressionContext context) {

		ShowableStringBuilder<List<TypeVariable>> print = new ShowableStringBuilder<List<TypeVariable>>(
				new ArrayList<TypeVariable>());
		print.print("Got type \"");
		print.print(type);
		print.print("\" expected \"");
		print.print(desired);
		print.print('"');
		print.print(message);
		errors.add(new ExpressionError(context.getAstContext(node), print
				.toString(), null));
		print.close();
	}

	/** Used by {@link AstNode}s during type checking phase to note any errors. */
	public void appendTypeError(Type type, Type desired, AstNode node,
			ExpressionContext context) {
		appendTypeError("", type, desired, node, context);
	}

	public UserEnvironment getEnvironment() {
		return environment;
	}

	public ExpressionRunListener getListener() {
		return listener;
	}

	public Entry<AstNode, FileContext> parseFile(File file) {
		FileContext context = new FileContext(file);
		AstNode node = null;
		StringBuilder sb = new StringBuilder();
		MultiLineContext linecontext = null;
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line;
			int linenumber = 0;
			while ((line = input.readLine()) != null) {
				linenumber++;
				if (line.isEmpty()) {
					/* Ignore. */
				} else if (Character.isWhitespace(line.charAt(0))) {
					if (linecontext == null) {
						errors
								.add(new ExpressionError(
										context.getContextForLine(linenumber,
												line),
										"Line starts with white space, but it is not continuing the previous line.",
										null));
						return null;
					} else {
						linecontext.append(context.getContextForLine(
								linenumber, line));
						sb.append(line);
					}
				} else {
					if (linecontext != null) {
						Parser parser = new Parser(this, linecontext, sb,
								listener);
						if (!parser.isEmpty()) {
							AstNode result = parser.parse();
							if (result == null) {
								return null;
							}
							if (node == null) {
								node = result;
							} else {
								node = new AstSequence(node, result);
							}
						}
					}
					linecontext = new MultiLineContext(context
							.getContextForLine(linenumber, line));
					sb.setLength(0);
					sb.append(line);
				}
			}
			input.close();
		} catch (IOException e) {
			errors.add(new ExpressionError(context, "Script error.", e));
			return null;
		}
		if (linecontext != null) {
			Parser parser = new Parser(this, linecontext, sb, listener);
			if (!parser.isEmpty()) {
				AstNode result = parser.parse();
				if (result == null) {
					return null;
				}
				if (node == null) {
					node = result;
				} else {
					node = new AstSequence(node, result);
				}
			}
		}
		return new ca.wlu.gisql.function.pair.Pair<AstNode, FileContext>(node,
				context);
	}

	@SuppressWarnings("unchecked")
	private boolean run(AstNode node, Type type, ExpressionContext context) {
		node = node.resolve(this, context, new BuiltInResolver(
				new EnvironmentResolver(environment)));
		if (node == null || !node.type(this, context)) {
			listener.reportErrors(new ArrayList<ExpressionError>(errors));
			errors.clear();
			return false;
		}

		if (type != null && !type.unify(node.getType())) {
			appendTypeError(node.getType(), type, node, context);
			listener.reportErrors(new ArrayList<ExpressionError>(errors));
			errors.clear();
			return false;
		}

		Class<? extends GenericFunction> program = node.render();
		if (program == null) {
			listener.reportErrors(errors.isEmpty() ? Collections
					.singletonList(new ExpressionError(context,
							"Failed to generate bytecode.", null))
					: new ArrayList<ExpressionError>(errors));
			errors.clear();
			return false;
		}

		if (!listener.previewAst(node)) {
			return false;
		}

		Object value = null;
		try {
			Constructor<? extends GenericFunction> ctor = (Constructor<? extends GenericFunction>) program
					.getConstructors()[0];
			GenericFunction function = ctor.newInstance(this);
			value = function.run();

			if (value == null
					&& !function.getType().canUnify(
							new MaybeType(new TypeVariable()))) {
				listener.reportErrors(Collections
						.singletonList(new ExpressionError(context,
								"Machine failure.", null)));
				return false;

			}

		} catch (IllegalArgumentException e) {
			log.error("Failed to access field.", e);
		} catch (SecurityException e) {
			log.error("Failed to access field.", e);
		} catch (InstantiationException e) {
			log.error("Failed to access field.", e);
		} catch (IllegalAccessException e) {
			log.error("Failed to access field.", e);
		} catch (InvocationTargetException e) {
			log.error("Failed to access field.", e);
		}

		if (value instanceof Interactome) {
			listener.processInteractome((Interactome) value);
		} else if (value instanceof List && ((List) value).size() > 0
				&& ((List) value).get(0) instanceof Interactome) {
			if (((List) value).size() > 0) {
				for (Object interactome : (List) value) {
					listener.processInteractome((Interactome) interactome);
				}
			} else {
				listener.processOther(type, value);
			}
		} else {
			listener.processOther(node.getType(), value);
		}

		return true;
	}

	/**
	 * Execute all commands in a file, formatted one entry per line.
	 * 
	 * @param file
	 *            File containing commands.
	 * @param type
	 *            The expected type of all commands in the file. It may be null
	 *            if any type is acceptable.
	 * @return True if execution was successful.
	 * */
	public boolean run(File file, Type type) {
		Entry<AstNode, FileContext> result = parseFile(file);
		if (result == null) {
			listener.reportErrors(errors);
			return false;
		} else {
			return run(result.getKey(), type, result.getValue());
		}
	}

	/**
	 * Execute a supplied commands.
	 * 
	 * @param command
	 *            The query language command to execute.
	 * @param type
	 *            The expected type of the command. It may be null if any type
	 *            is acceptable.
	 * @return True if execution was successful.
	 * */
	public boolean run(String command, Type type) {
		return run(command, type, new SingleLineContext(command));
	}

	private boolean run(String command, Type type, LineContext context) {
		Parser parser = new Parser(this, context, command, listener);
		if (parser.isEmpty()) {
			return true;
		}
		AstNode result = parser.parse();
		if (result == null) {
			return false;
		}

		return run(result, type, context);
	}

	public boolean typeCheck(AstNode node, Type type, ExpressionContext context) {
		if (node.type(this, context)) {
			if (node.getType().unify(type)) {
				return true;
			} else {
				appendTypeError(node.getType(), type, node, context);
				return false;
			}
		} else {
			return false;
		}
	}

}
