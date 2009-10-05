package ca.wlu.gisql.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.ProgramRoutine;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class ExpressionRunner {
	private final UserEnvironment environment;

	private final List<ExpressionError> errors = new ArrayList<ExpressionError>();

	private final ExpressionRunListener listener;

	public ExpressionRunner(UserEnvironment environment,
			ExpressionRunListener listener) {
		super();
		this.environment = environment;
		this.listener = listener;
	}

	public void appendResolutionError(String message, AstNode node,
			ExpressionContext context) {
		errors.add(new ExpressionError(context.getAstContext(node), message,
				null));
	}

	public void appendTypeError(Type type, Type desired, AstNode node,
			ExpressionContext context) {
		ShowableStringBuilder<List<TypeVariable>> print = new ShowableStringBuilder<List<TypeVariable>>(
				new ArrayList<TypeVariable>());
		print.print("Got type \"");
		print.print(type);
		print.print("\" expected \"");
		print.print(desired);
		print.print('"');
		errors.add(new ExpressionError(context.getAstContext(node), print
				.toString(), null));
	}

	public boolean run(File file, Type type) {
		FileContext context = new FileContext(file);
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line;
			int linenumber = 0;
			while ((line = input.readLine()) != null) {
				linenumber++;
				if (!run(line, type, context
						.getContextForLine(linenumber, line))) {
					return false;
				}
			}
			input.close();
			return true;
		} catch (IOException e) {
			listener.reportErrors(Collections
					.singletonList(new ExpressionError(context,
							"Script error.", e)));
		}
		return true;
	}

	public boolean run(String command, Type type) {
		return run(command, type, new SingleLineContext(command));
	}

	@SuppressWarnings("unchecked")
	private boolean run(String command, Type type, LineContext context) {
		Parser parser = new Parser(environment, context, command, listener);
		AstNode result = parser.parse();
		if (result == null) {
			return false;
		}

		result = result.resolve(this, context, environment);
		if (result == null || !result.type(this, context)) {
			listener.reportErrors(errors);
			return false;
		}

		if (type != null && !type.unify(result.getType())) {
			appendTypeError(result.getType(), type, result, context);
			listener.reportErrors(errors);
			return false;
		}

		ProgramRoutine program = new ProgramRoutine(result.toString());
		if (!result.render(program, 0, -1)) {
			listener.reportErrors(errors);
			return false;
		}

		Object value = program.run(listener, environment);

		if (value == null) {
			listener.reportErrors(Collections
					.singletonList(new ExpressionError(context,
							"Machine failure.", null)));
			return false;

		}

		if (result.getType().unify(Type.InteractomeType)) {
			listener.processInteractome((Interactome) value);
		} else if (result.getType().unify(new ListType(Type.InteractomeType))) {
			for (Object interactome : (List) value) {
				listener.processInteractome((Interactome) interactome);
			}
		} else {
			listener.processOther(result.getType(), value);
		}

		return true;
	}

}
