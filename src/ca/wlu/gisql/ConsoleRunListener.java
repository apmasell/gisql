package ca.wlu.gisql;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ProcessableInteractome;
import ca.wlu.gisql.interactome.output.AbstractOutput;
import ca.wlu.gisql.runner.AstContext;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunListener;
import ca.wlu.gisql.runner.FileContext;
import ca.wlu.gisql.runner.FileLineContext;
import ca.wlu.gisql.runner.PositionContext;
import ca.wlu.gisql.runner.SingleLineContext;

/** Puts results of computation on to the console. */
public class ConsoleRunListener implements ExpressionRunListener {
	private static final Logger log = Logger
			.getLogger(ConsoleRunListener.class);
	private final UserEnvironment environment;

	public ConsoleRunListener(UserEnvironment environment) {
		super();
		this.environment = environment;
	}

	/** Convert an {@link ExpressionContext} into a text representation. */
	private void buildContext(StringBuilder sb, ExpressionContext context) {
		if (context instanceof PositionContext) {
			PositionContext positioncontext = (PositionContext) context;

			buildContext(sb, positioncontext.getParent());

			sb.append(' ').append(positioncontext.getPosition());
		} else if (context instanceof SingleLineContext) {
			SingleLineContext singlelinecontext = (SingleLineContext) context;

			sb.append(singlelinecontext.getLine()).append('\n');
			sb.append("<stdin>");
		} else if (context instanceof FileLineContext) {
			FileLineContext filelinecontext = (FileLineContext) context;

			sb.append(filelinecontext.getSource()).append(" +").append(
					filelinecontext.getLineNumber());
		} else if (context instanceof FileContext) {
			FileContext filecontext = (FileContext) context;
			sb.append(filecontext.getFile().getName());
		} else if (context instanceof AstContext) {
			AstContext astcontext = (AstContext) context;

			buildContext(sb, astcontext.getParent());

			sb.append(" in expression (").append(astcontext.getNode()).append(
					")");
		} else {
			sb.append("<unknown>");
		}
	}

	@Override
	public boolean previewAst(AstNode node) {
		return true;
	}

	private void print(Object value) {
		if (value == null) {
			System.out.print("missing");
		} else if (value instanceof String) {
			System.out.print('"');
			System.out.print(value);
			System.out.print('"');
		} else if (value instanceof List<?>) {
			System.out.print("[");
			boolean first = true;
			for (Object item : (List<?>) value) {
				if (first) {
					first = false;
				} else {
					System.out.print(", ");
				}
				print(item);
			}
			System.out.print("]");
		} else {
			System.out.print(value.toString());
		}
	}

	public void processInteractome(Interactome value) {
		ProcessableInteractome interactome = AbstractOutput.wrap(value, null,
				environment.getFormat(), environment.getOutput(), false);
		if (!interactome.process()) {
			log.error("Failed to process interactome");
		}
	}

	public void processOther(Type type, Object value) {
		print(value);
		System.out.println();
	}

	public void reportErrors(Collection<ExpressionError> errors) {
		for (ExpressionError error : errors) {
			StringBuilder sb = new StringBuilder();

			buildContext(sb, error.getContext());

			sb.append(": ").append(error.getMessage());
			if (error.getException() == null) {
				log.error(sb.toString());
			} else {
				log.error(sb.toString(), error.getException());
			}
		}
	}
}
