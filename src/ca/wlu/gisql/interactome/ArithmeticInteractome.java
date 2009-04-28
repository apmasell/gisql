package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.Iterator;

import org.apache.log4j.Logger;

import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.gene.CompositeGene;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.gene.RecalculatedGene;
import ca.wlu.gisql.interaction.CompositeInteraction;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.interaction.TranslatedInteraction;

public abstract class ArithmeticInteractome extends AbstractInteractome {
	static final Logger log = Logger.getLogger(ArithmeticInteractome.class);

	protected Interactome left, right;

	private TriangularNorm norm;

	public ArithmeticInteractome(TriangularNorm norm, Interactome left,
			Interactome right) {
		this.left = left;
		this.right = right;
		this.norm = norm;
	}

	private final double calculateAdjustedGeneMembership(int numNewOrthologies,
			Gene gene, Gene ortholog) {
		double geneMembership = (gene == null ? 0 : gene.getMembership());
		int geneOrthologies = (gene == null ? 0 : gene.getNumberOfOrthologies());
		int geneSize = left.numGenomes();

		double orthologMembership = (ortholog == null ? 0 : ortholog
				.getMembership());
		int orthologOrthologies = (gene == null ? 0 : gene
				.getNumberOfOrthologies());
		int orthologSize = right.numGenomes();

		double averagemembership = (geneSize * geneMembership + orthologSize
				* orthologMembership)
				/ (geneSize + orthologSize);

		int totalOrthologies = 2 * numNewOrthologies + geneOrthologies
				+ orthologOrthologies;
		double geneFactor = (totalOrthologies == 0 ? 1
				: (numNewOrthologies + geneOrthologies)
						/ ((double) totalOrthologies));
		double orthologFactor = (totalOrthologies == 0 ? 1 : 1 - geneFactor);

		double geneAdjustedMembership = clipMembership(geneMembership
				* geneFactor + averagemembership * orthologFactor);
		double orthologAdjustedMembership = clipMembership(orthologMembership
				* orthologFactor + averagemembership * geneFactor);

		return calculateMembership(norm, geneAdjustedMembership,
				orthologAdjustedMembership);
	}

	protected abstract double calculateMembership(TriangularNorm norm,
			double left, double right);

	private double clipMembership(double membership) {
		if (membership < 0)
			return 0;
		if (membership > 1)
			return 1;
		return membership;
	}

	public int countOrthologs(Gene gene) {
		return left.countOrthologs(gene) + right.countOrthologs(gene);
	}

	public final Gene findRootOrtholog(Gene gene) {
		/*
		 * We seek to put orthlogs in the reference point of interactome #1
		 * where possible. If there exists an orthlog in that genome, use it
		 * preferentially.
		 */
		Gene ortholog = left.findOrtholog(gene);
		return (ortholog != null ? ortholog : right.findOrtholog(gene));
	}

	public abstract char getSymbol();

	protected final double membershipOfUnknown() {
		return calculateMembership(norm, 0, 0);
	}

	public final int numGenomes() {
		return left.numGenomes() + left.numGenomes();
	}

	protected final void prepareInteractions() {
		Iterator<Interaction> itLeftInteraction = left.iterator();
		Iterator<Interaction> itRightInteraction = right.iterator();

		System.gc();

		log.info("Computing left genes");
		for (Gene gene : left.genes()) {
			Gene ortholog = right.findOrtholog(gene);
			if (ortholog == null) {
				addGene(new RecalculatedGene(gene,
						calculateAdjustedGeneMembership(0, gene, null)));
			} else {
				int newOrthologies = gene.countOrthologs(right);
				addGene(new CompositeGene(gene, ortholog,
						calculateAdjustedGeneMembership(newOrthologies, gene,
								ortholog), newOrthologies));
			}
		}
		log.info("Computing right genes");
		for (Gene gene : right.genes()) {
			if (this.findOrtholog(gene) == null) {
				addGene(new RecalculatedGene(gene,
						calculateAdjustedGeneMembership(0, null, gene)));
			}
		}

		log.info("Computing left interactions");

		while (itLeftInteraction.hasNext()) {
			Interaction interaction = itLeftInteraction.next();

			Interaction orthoaction = right.getInteraction(right
					.findOrtholog(interaction.getGene1()), right
					.findOrtholog(interaction.getGene2()));
			if (orthoaction == null) {
				addInteraction(new TranslatedInteraction(this, interaction,
						this.findOrtholog(interaction.getGene1()), this
								.findOrtholog(interaction.getGene2()),
						calculateMembership(norm, interaction.getMembership(),
								0)));
			} else {
				double membership = calculateMembership(norm, interaction
						.getMembership(), orthoaction.getMembership());
				Interaction i = new CompositeInteraction(this, interaction,
						orthoaction, membership);
				addInteraction(i);

			}
		}
		log.info("Computing right interactions");
		while (itRightInteraction.hasNext()) {
			Interaction interaction = itRightInteraction.next();
			Gene ortholog1 = left.findOrtholog(interaction.getGene1());
			Gene ortholog2 = left.findOrtholog(interaction.getGene2());
			if (ortholog1 == null || ortholog2 == null
					|| left.getInteraction(ortholog1, ortholog2) == null) {
				addInteraction(new TranslatedInteraction(left, interaction,
						this.findOrtholog(interaction.getGene1()), this
								.findOrtholog(interaction.getGene2()),
						calculateMembership(norm, 0, interaction
								.getMembership())));
			}
		}
		log.info("Set operation complete");
	}

	public final PrintStream show(PrintStream print) {
		print.print("(");
		left.show(print);
		print.print(" ");
		print.print(getSymbol());
		print.print(" ");
		right.show(print);
		print.print(")");
		return print;
	}

	public final StringBuilder show(StringBuilder sb) {
		sb.append("(");
		left.show(sb);
		sb.append(" ").append(getSymbol()).append(" ");
		right.show(sb);
		sb.append(")");
		return sb;
	}
}
