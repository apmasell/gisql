package ca.wlu.gisql.ast.functions;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import ca.wlu.gisql.ast.AstNative;
import ca.wlu.gisql.ast.type.MaybeType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Rendering;

public class NullDefault extends AstNative {
	private static final TypeVariable a = new TypeVariable();

	public NullDefault() {
		super(
				"otherwise",
				"Checks if a nullable value is missing and replaces it with a default.",
				new MaybeType(a), a, a);
	}

	@Override
	protected <C> boolean renderSelf(Rendering<C> program, int depth) {
		Label label = new Label();
		return program.pPg() && program.lOhO()
				&& program.jump(Opcodes.IFNONNULL, label) && program.pO()
				&& program.pPg() && program.mark(label);
	}
}
