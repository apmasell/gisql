package ca.wlu.gisql.ast.util;

/**
 * Implementors of this method can be converted to bytecode using a
 * {@link Rendering} instance.
 */
public interface Renderable {

	<C> boolean render(Rendering<C> rendering, int depth);

}
