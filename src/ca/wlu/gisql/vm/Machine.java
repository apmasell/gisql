package ca.wlu.gisql.vm;

import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.runner.ExpressionRunListener;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Machine {

	private static final Logger log = Logger.getLogger(Machine.class);
	final UserEnvironment environment;
	Frame frame = Frame.java;
	private final ExpressionRunListener listener;
	final Stack<Object> operands = new Stack<Object>();
	final Stack<Frame> stack = new Stack<Frame>();
	final Stack<Object> variables = new Stack<Object>();

	public Machine(ExpressionRunListener listener, UserEnvironment environment) {
		super();
		this.listener = listener;
		this.environment = environment;
	}

	public Machine duplicate() {
		Machine duplicate = new Machine(listener, environment);
		duplicate.variables.addAll(variables);
		return duplicate;
	}

	public Object enter(Program program, Object... parameters) {
		if (parameters.length > 0) {
			for (int index = parameters.length - 1; index >= 0; index--) {
				operands.push(parameters[index]);
			}
			operands.push(program);
			program = new ApplyProgram(parameters.length);
		}
		stack.push(frame);
		stack.push(Frame.java);
		frame = new Frame(program);
		while (frame != Frame.java) {
			frame.program.get(frame.counter++).execute(this);
		}

		frame = stack.pop();
		return operands.pop();
	}

	protected void error(Exception e) {
		log.error("Exception in machine: " + toString(), e);
	}

	public UserEnvironment getEnvironment() {
		return environment;
	}

	public ExpressionRunListener getListener() {
		return listener;
	}

	private void showFrame(ShowablePrintWriter<Object> print, Frame frame) {
		if (frame == Frame.java) {
			print.print("{ Java }\n");
		} else {
			print.print("PC: ");
			print.print(frame.counter);
			print.print(": ");
			print.print(frame.program.get(frame.counter));
			print.println();
			print.print(frame.program);
			print.println();
		}
	}

	@Override
	public String toString() {
		ShowableStringBuilder<Object> print = new ShowableStringBuilder<Object>(
				null);

		boolean first;
		print.print("OP:");
		first = true;
		for (Object object : operands) {
			if (first) {
				first = false;
			} else {
				print.print(", ");
			}
			print.print(object);
		}
		print.println();

		print.print("V:");
		first = true;
		for (Object object : variables) {
			if (first) {
				first = false;
			} else {
				print.print(", ");
			}
			print.print(object);
		}
		print.println();

		showFrame(print, frame);

		print.print("S:\n");
		for (Frame frame : stack) {
			showFrame(print, frame);
		}

		return print.toString();
	}
}
