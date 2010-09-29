package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;

/**
 * A variable which is really an index into a tuple.
 */
public class AstPairAccessor extends SyntheticAccessor {

	private final String parent;

	private final boolean[] selectors;

	public AstPairAccessor(String parent, String name, Type type,
			boolean... selectors) {
		super(name, type);
		this.parent = parent;
		this.selectors = selectors;
	}

	@Override
	public ResolutionEnvironment getModifiedEnvironment(
			ResolutionEnvironment environment) {
		return environment;
	}

	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		return program.lRhO_PairAccess(parent, type.getRootJavaType(),
				selectors);
	}

}