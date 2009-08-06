package ca.wlu.gisql.interactome.proximity;

import java.util.Set;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.delay.Delay;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstProximity implements AstNode {

	private Set<Long> accessions;
	private AstNode parameter;
	private int radius;

	public AstProximity(AstNode parameter, int radius, Set<Long> accessions) {
		this.parameter = parameter;
		this.radius = radius;
		this.accessions = accessions;
	}

	public Interactome asInteractome() {
		return new Proximity(new Delay(parameter.asInteractome()), radius,
				accessions);
	}

	public AstNode fork(AstNode substitute) {
		return new AstProximity(parameter.fork(substitute), radius, accessions);
	}

	public int getPrecedence() {
		return Proximity.descriptor.getPrecedence();
	}

	public boolean isInteractome() {
		return true;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(parameter, getPrecedence());
		print.print(":near (");
		boolean first = true;
		for (long accession : accessions) {
			if (first) {
				first = false;
			} else {
				print.print(", ");
			}
			print.print(accession);
		}
		print.print(")");
		if (radius < Integer.MAX_VALUE) {
			print.print(' ');
			print.print(radius);
		}
	}
}
