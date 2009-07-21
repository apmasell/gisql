package ca.wlu.gisql.gui;

import java.awt.Component;

import ca.wlu.gisql.environment.parser.ast.AstVoid;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class ExecutableTask<P extends Component & TaskParent> extends
		ComputationalTask<P> {
	private final AstVoid executable;

	public ExecutableTask(P parent, AstVoid executable) {
		super(parent, ShowableStringBuilder.toString(executable, executable));
		this.executable = executable;
	}

	protected boolean doIt() {
		executable.execute();
		return true;
	}

	protected CachedInteractome getResult() {
		return null;
	}

}