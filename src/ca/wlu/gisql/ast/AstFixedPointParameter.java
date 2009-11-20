package ca.wlu.gisql.ast;

import java.util.Set;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * The self-reference represented by a fixed-point expression. (i.e., the
 * <b>f</b> in <tt>Y (λ f. (λ x. if (gt x 0) (<b>f</b> (sub x 1)) 0))</tt>.
 */
public class AstFixedPointParameter extends AstNode {

	private static final Logger log = Logger.getLogger(Rendering.class);

	String classname;

	int index = -1;

	final String name;

	final TypeVariable type = new TypeVariable();

	public AstFixedPointParameter(String name) {
		this.name = name;
	}

	@Override
	protected void freeVariables(Set<String> variables) {
		variables.add(name);
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public boolean renderSelf(Rendering program, int depth) {
		try {
			return program.lRhO(name)
					&& program.pPg$hO_BoxArguments(type.getArrowDepth())
					&& program.g_InvokeMethod(GenericFunction.class
							.getDeclaredMethod("run", Object[].class));
		} catch (SecurityException e) {
			log.error("Failed to get GenericFunction.run method.", e);
		} catch (NoSuchMethodException e) {
			log.error("Failed to get GenericFunction.run method.", e);
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
		return this;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(name);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return true;
	}

}