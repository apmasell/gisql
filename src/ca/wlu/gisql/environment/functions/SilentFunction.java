package ca.wlu.gisql.environment.functions;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ProcessableInteractome;
import ca.wlu.gisql.vm.Machine;

public final class SilentFunction extends Function {
	public static final Function self = new SilentFunction();

	private SilentFunction() {
		super("do", "Runs a query and supresses the results",
				Type.InteractomeType, Type.UnitType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		ProcessableInteractome processableInteractome = CachedInteractome.wrap(
				(Interactome) parameters[0], null);
		processableInteractome.process();
		return Unit.nil;
	}

}
