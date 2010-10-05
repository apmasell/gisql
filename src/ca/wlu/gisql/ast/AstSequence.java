package ca.wlu.gisql.ast;

import java.lang.reflect.Method;
import java.util.Iterator;

import name.masella.iterator.ArrayIterator;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunListener;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstSequence extends AstNode {
	enum Handling {
		ProcessInteractome {
			@Override
			public <C> boolean process(Rendering<C> program, AstNode node) {
				return program.lNhO() && program.g_InvokeMethod(getListener)
						&& node.render(program, 0)
						&& program.g_Cast(Interactome.class)
						&& program.g_InvokeMethod(processInteractome);
			}
		},
		ProcessOther {
			@Override
			public <C> boolean process(Rendering<C> program, AstNode node) {
				return program.lNhO() && program.g_InvokeMethod(getListener)
						&& node.getType().render(program, 0)
						&& node.render(program, 0)
						&& program.g_InvokeMethod(processOther);
			}
		},
		ThrowAway {
			@Override
			public <C> boolean process(Rendering<C> program, AstNode node) {
				return node.render(program, 0) && program.pO();
			}
		};

		abstract <C> boolean process(Rendering<C> program, AstNode node);
	}

	private final static Method getListener;

	private static final Logger log = Logger.getLogger(AstSequence.class);

	private final static Method processInteractome;
	private final static Method processOther;
	static {
		Method getListenerMethod = null;
		Method processInteractomeMethod = null;
		Method processOtherMethod = null;
		try {
			getListenerMethod = ExpressionRunner.class.getMethod("getListener");
			processInteractomeMethod = ExpressionRunListener.class.getMethod(
					"processInteractome", Interactome.class);
			processOtherMethod = ExpressionRunListener.class.getMethod(
					"processOther", Type.class, Object.class);
		} catch (SecurityException e) {
			log.error("Cannot access methods.", e);
		} catch (NoSuchMethodException e) {
			log.error("Cannot access methods.", e);
		}
		getListener = getListenerMethod;
		processInteractome = processInteractomeMethod;
		processOther = processOtherMethod;
	}
	private final AstNode first;

	private final AstNode second;

	public AstSequence(AstNode first, AstNode second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		first.freeVariables(variables);
		second.freeVariables(variables);
	}

	@Override
	public ResolutionEnvironment getModifiedEnvironment(
			ResolutionEnvironment environment) {
		return second.getModifiedEnvironment(first
				.getModifiedEnvironment(environment));
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Assignment;
	}

	@Override
	public Type getType() {
		return second.getType();
	}

	@Override
	public Iterator<AstNode> iterator() {
		return new ArrayIterator<AstNode>(first, second);
	}

	@Override
	protected <C> boolean renderSelf(Rendering<C> program, int depth) {
		Handling handling;
		if (first.getType().canUnify(Type.UnitType)) {
			handling = Handling.ThrowAway;
		} else if (first.getType().canUnify(Type.InteractomeType)) {
			handling = Handling.ProcessInteractome;
		} else {
			handling = Handling.ProcessOther;
		}

		return handling.process(program, first)
				&& second.render(program, depth);
	}

	@Override
	public void resetType() {
		first.resetType();
		second.resetType();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		AstNode resultfirst = first.resolve(runner, context, environment);
		if (resultfirst == null) {
			return null;
		}
		AstNode resultsecond = second.resolve(runner, context, resultfirst
				.getModifiedEnvironment(environment));
		if (resultsecond == null) {
			return null;
		}
		return new AstSequence(resultfirst, resultsecond);
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.println(first);
		print.print(second);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return first.type(runner, context) && second.type(runner, context);
	}

}
