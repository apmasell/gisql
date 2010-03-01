package ca.wlu.gisql.runner;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.UserEnvironment;

/**
 * A version of the {@link ExpressionRunner} that does the work in a separate
 * thread. The listener may be invoked in any thread.
 */
public class ThreadedExpressionRunner extends ExpressionRunner {

	private abstract class Action {
		abstract void perform();
	}

	private class RunFile extends Action {
		final File file;
		final Type type;

		public RunFile(File file, Type type) {
			super();
			this.file = file;
			this.type = type;
		}

		@Override
		void perform() {
			run(file, type);
		}
	}

	private class Runner implements Runnable {

		public void run() {
			while (true) {
				try {
					Action action = todo.take();
					if (action == null) {
						return;
					}

					action.perform();
				} catch (InterruptedException e) {
					log.error("Interruption while reading from queue", e);
					return;
				}
			}
		}
	}

	private class RunString extends Action {
		final String line;
		final Type type;

		public RunString(String line, Type type) {
			super();
			this.line = line;
			this.type = type;
		}

		@Override
		void perform() {
			run(line, type);
		}
	}

	private static final Logger log = Logger
			.getLogger(ThreadedExpressionRunner.class);

	private boolean busy = false;

	private final BlockingQueue<Action> todo = new ArrayBlockingQueue<Action>(
			100);

	private final Thread worker;

	public ThreadedExpressionRunner(UserEnvironment environment,
			ExpressionRunListener listener) {
		super(environment, listener);
		worker = new Thread(new Runner());
		worker.start();
	}

	/** Check if an expression is currently being processed. */
	public boolean isBusy() {
		return busy;
	}

	@Override
	public boolean run(File file, Type type) {
		if (Thread.currentThread() == worker) {
			busy = true;
			boolean result = super.run(file, type);
			busy = false;
			return result;
		} else {
			try {
				todo.put(new RunFile(file, type));
				return true;
			} catch (InterruptedException e) {
				log.error("Could not send run(file) command.", e);
				return false;
			}
		}
	}

	@Override
	public boolean run(String command, Type type) {
		if (Thread.currentThread() == worker) {
			busy = true;
			boolean result = super.run(command, type);
			busy = false;
			return result;
		} else {
			try {
				todo.put(new RunString(command, type));
				return true;
			} catch (InterruptedException e) {
				log.error("Could not send run(string) command.", e);
				return false;
			}
		}
	}

}
