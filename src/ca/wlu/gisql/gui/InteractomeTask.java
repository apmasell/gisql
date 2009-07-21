package ca.wlu.gisql.gui;

import java.awt.Component;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class InteractomeTask<P extends Component & TaskParent> extends
		ComputationalTask<P> {
	private final CachedInteractome interactome;

	public InteractomeTask(P parent, CachedInteractome interactome) {
		super(parent, ShowableStringBuilder.toString(interactome, GisQL
				.collectAll(interactome)));
		this.interactome = interactome;
	}

	protected boolean doIt() {
		return interactome.process();
	}

	protected CachedInteractome getResult() {
		return interactome;
	}
}