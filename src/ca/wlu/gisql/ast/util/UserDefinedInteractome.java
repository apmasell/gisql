package ca.wlu.gisql.ast.util;

import java.util.Set;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public abstract class UserDefinedInteractome implements Interactome {

	private final String $definition;

	protected final Interactome[] $interactomes;

	private final double $membership;

	protected UserDefinedInteractome(String definition,
			Interactome[] interactomes, Double membership) {
		super();
		$membership = membership;
		$definition = definition;
		$interactomes = interactomes;
	}

	@Override
	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		for (Interactome interactome : $interactomes) {
			interactome.collectAll(set);
		}
		return set;
	}

	@Override
	public Construction getConstruction() {
		return Construction.Computed;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public double membershipOfUnknown() {
		return $membership;
	}

	@Override
	public boolean postpare() {
		boolean result = true;
		for (Interactome interactome : $interactomes) {
			result &= interactome.postpare();
		}
		return true;
	}

	@Override
	public boolean prepare() {
		for (Interactome interactome : $interactomes) {
			if (!interactome.prepare()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print($definition);
		for (Interactome interactome : $interactomes) {
			print.print(' ');
			print.print(interactome);
		}
	}

}
