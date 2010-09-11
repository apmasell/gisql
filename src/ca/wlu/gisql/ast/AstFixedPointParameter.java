package ca.wlu.gisql.ast;

import java.util.Iterator;

import org.apache.commons.collections15.iterators.EmptyIterator;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.ast.util.MaskedEnvironment;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * The self-reference represented by a fixed-point expression. (i.e., the
 * <b>f</b> in <tt>Y (λ f. (λ x. if (gt x 0) (<b>f</b> (sub x 1)) 0))</tt>.
 */
public class AstFixedPointParameter extends NamedVariable {

	private static final Logger log = Logger
			.getLogger(AstFixedPointParameter.class);

	final String name;

	final Type type = new ArrowType(new TypeVariable(), new TypeVariable());

	final VariableInformation variableInformation;

	public AstFixedPointParameter(String name) {
		this.name = name;
		variableInformation = new VariableInformation(name, type);
	}

	@Override
	ResolutionEnvironment createEnvironment(ResolutionEnvironment environment) {
		return new MaskedEnvironment<AstFixedPointParameter>(this, environment);
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		variables.add(variableInformation);
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public String getVariableName() {
		return name;
	}

	@Override
	public Iterator<AstNode> iterator() {
		return EmptyIterator.getInstance();
	}

	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
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