package ca.wlu.gisql.interactome;

import javax.swing.table.TableModel;

import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.Show;

public interface Interactome extends Iterable<Interaction>, TableModel, Show {

    public abstract long findOrtholog(long gene);

    public abstract long getComputationTime();

    public abstract Interaction getInteraction(long gene1, long gene2);

    public abstract boolean process();

}