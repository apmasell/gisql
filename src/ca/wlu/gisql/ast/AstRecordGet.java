package ca.wlu.gisql.ast;

import java.util.Map;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.RecordType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Representation of a field access. That is, it extracts the named member of a
 * record.
 */
public class AstRecordGet extends AstNode {
	private static final Logger log = Logger.getLogger(AstRecordGet.class);
	private final String field;
	private final AstNode parameter;
	private final RecordType parametertype;

	private final TypeVariable type = new TypeVariable();

	public AstRecordGet(AstNode parameter, String field) {
		this.parameter = parameter;
		this.field = field;
		parametertype = new RecordType(field, type);
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		parameter.freeVariables(variables);
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.UnaryPostfix;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	protected <T> boolean renderSelf(Rendering<T> program, int depth) {
		try {
			return parameter.render(program, depth)
					&& program.hO(field)
					&& program.g_InvokeMethod(Map.class.getDeclaredMethod(
							"get", Object.class))
					&& program.g_Cast(type.getRootJavaType());
		} catch (SecurityException e) {
			log.error("Failed to get Record.get method.", e);

		} catch (NoSuchMethodException e) {
			log.error("Failed to get Record.get method.", e);

		}
		return false;
	}

	@Override
	public void resetType() {
		type.reset();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		AstNode parameter = this.parameter
				.resolve(runner, context, environment);
		if (parameter == null) {
			return null;
		} else {
			return new AstRecordGet(parameter, field);
		}
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(parameter);
		print.print('.');
		print.print(field);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		if (parameter.type(runner, context)) {
			if (!parameter.getType().unify(parametertype)) {
				runner.appendTypeError(parameter.getType(), parametertype,
						this, context);
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

}
