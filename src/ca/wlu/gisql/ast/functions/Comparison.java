package ca.wlu.gisql.ast.functions;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import ca.wlu.gisql.ast.AstNative;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Rendering;

public class Comparison extends AstNative {
	private static final TypeVariable a = new TypeVariable();

	public Comparison() {
		super("if", "Makes a decision", Type.BooleanType, a, a, a);
	}

	@Override
	public boolean renderSelf(Rendering program, int depth) {
		Label elsepart = new Label();
		Label end = new Label();
		return program.pPg() && program.pOhO_ObjectToPrimitive(Boolean.class)
				&& program.jump(Opcodes.IFEQ, elsepart) && program.pPg()
				&& program.jump(Opcodes.GOTO, end) && program.mark(elsepart)
				&& program.pPg() && program.mark(end);
	}

}
