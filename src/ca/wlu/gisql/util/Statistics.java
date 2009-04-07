package ca.wlu.gisql.util;

import java.io.PrintStream;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;

public class Statistics implements Show {
    private double geneFuziness = 0;

    private int genes = 0;

    private double interactionFuzziness = 0;

    private int interactions = 0;

    private int[] interactionBins;

    private double offset;

    private double binwidth;

    private int[] geneBins;

    private int maxbin;

    public Statistics(int bincount, double lowerbound, double upperbound) {
	super();
	maxbin = bincount - 1;
	binwidth = bincount / (upperbound - lowerbound);
	offset = binwidth * lowerbound;
	geneBins = new int[bincount];
	interactionBins = new int[bincount];
    }

    private int calculateBin(double membership) {
	return Math.min((int) (membership * binwidth - offset), maxbin);
    }

    public void count(Gene gene) {
	double membership = gene.getMembership();
	genes++;
	geneFuziness += 1 - Math.abs(2 * membership - 1);
	geneBins[calculateBin(membership)]++;
    }

    public void count(Interaction interaction) {
	double membership = interaction.getMembership();
	interactions++;
	interactionFuzziness += 1 - Math.abs(2 * membership - 1);
	interactionBins[calculateBin(membership)]++;
    }

    public PrintStream show(PrintStream print) {
	print.print("# ");
	print.print(genes);
	print.print(" genes in ");
	print.print(interactions);
	print.print(" interactions. Gene fuzziness: ");
	print.print(geneFuziness);
	print.print(" Interaction fuzziness: ");
	print.print(interactionFuzziness);
	print.print("\n# Gene histogram: ");
	for (int i : geneBins) {
	    print.print(i);
	    print.print(" ");
	}
	print.print("\n# Interaction histogram: ");
	for (int i : interactionBins) {
	    print.print(i);
	    print.print(" ");
	}
	return print;
    }

    public StringBuilder show(StringBuilder sb) {
	sb.append("# ");
	sb.append(genes);
	sb.append(" genes in ");
	sb.append(interactions);
	sb.append(" interactions. Gene fuzziness: ");
	sb.append(geneFuziness);
	sb.append(" Interaction fuzziness: ");
	sb.append(interactionFuzziness);
	sb.append("\n#G ene histogram: ");
	for (int i : geneBins)
	    sb.append(i).append(" ");
	sb.append("\n#Interaction histogram: ");
	for (int i : interactionBins)
	    sb.append(i).append(" ");
	return sb;
    }

}