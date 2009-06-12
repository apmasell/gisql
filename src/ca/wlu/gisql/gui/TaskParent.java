package ca.wlu.gisql.gui;

import ca.wlu.gisql.interactome.CachedInteractome;

public interface TaskParent {
	void processedInteractome(CachedInteractome interactome);
}