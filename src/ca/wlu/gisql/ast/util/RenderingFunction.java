package ca.wlu.gisql.ast.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ca.wlu.gisql.runner.ExpressionRunner;

public class RenderingFunction extends Rendering<GenericFunction> {

	/**
	 * Create a new block of dynamic code.
	 * 
	 * @param representation
	 *            how this block of code should be presented to the user.
	 * @param type
	 *            the query-language type of this code.
	 * @param argumentcount
	 *            the number of arguments that are going to be provided as
	 *            arguments to the {@link GenericFunction#run(Object...)}
	 *            method.
	 */
	public RenderingFunction(String representation,
			ca.wlu.gisql.ast.type.Type type,
			ca.wlu.gisql.ast.type.Type... arguments) {
		super("GisqlFunction", GenericFunction.class);

		startMethod(ACC_PUBLIC, "<init>", null, ExpressionRunner.class);
		method.visitVarInsn(ALOAD, 0);
		method.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
				"()V");
		method.visitVarInsn(ALOAD, 0);
		method.visitVarInsn(ALOAD, 1);
		method.visitFieldInsn(PUTFIELD, name, FieldRunner, TypeRunner);
		method.visitInsn(Opcodes.RETURN);
		endMethod();

		/* toString() */
		startMethod(ACC_PUBLIC, "toString", String.class);
		method.visitLdcInsn(representation);
		method.visitInsn(ARETURN);
		endMethod();

		/* getDescription() */
		startMethod(ACC_PUBLIC, "getDescription", String.class);
		method.visitLdcInsn("User defined function: " + representation);
		method.visitInsn(ARETURN);
		endMethod();

		/* getType() */
		startMethod(ACC_PUBLIC, "getType", ca.wlu.gisql.ast.type.Type.class);
		if (!type.render(this, 0)) {
			throw new IllegalStateException("Unable to render type " + type
					+ ".");
		}
		method.visitInsn(ARETURN);
		endMethod();

		startMethod(ACC_PUBLIC, "run", Object.class, Object[].class);
		for (int offset = arguments.length-1; offset >=0; offset--) {
			ArgumentVariable variable = new ArgumentVariable(offset, Type
					.getInternalName(arguments[offset].getRootJavaType()));
			references.push(variable);
			parameters.push(variable);
		}
	}

	@Override
	protected void cleanupMethod() {
		method.visitInsn(ARETURN);
	}
}
