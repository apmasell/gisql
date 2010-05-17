package ca.wlu.gisql.ast.util;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionRunner;

public class RenderingInteractome extends Rendering<UserDefinedInteractome> {

	private final String[] variables;

	public RenderingInteractome(AstNode membership, String descriptor,
			String[] variables) {
		super("GisqlInteractome", UserDefinedInteractome.class);
		this.variables = variables.clone();
		Class<?>[] parameters = new Class<?>[1 + variables.length];
		parameters[0] = ExpressionRunner.class;
		for (int index = 1; index < parameters.length; index++) {
			parameters[index] = Interactome.class;
		}

		startMethod(ACC_PUBLIC, "<init>", null, parameters);
		method.visitVarInsn(ALOAD, 0);
		method.visitLdcInsn(descriptor);
		method.visitLdcInsn(variables.length);
		method
				.visitTypeInsn(ANEWARRAY, Type
						.getInternalName(Interactome.class));
		String signature = makeSignature(double.class);
		String interactomeinterface = Type.getInternalName(Interactome.class);
		for (int index = 0; index < variables.length; index++) {
			method.visitInsn(DUP);
			method.visitLdcInsn(index);
			method.visitVarInsn(ALOAD, 2 + index);
			method.visitTypeInsn(CHECKCAST, Type
					.getInternalName(Interactome.class));
			method.visitInsn(DUP);
			method.visitMethodInsn(INVOKEINTERFACE, interactomeinterface,
					"membershipOfUnknown", signature);
			pOhO_PrimitiveToObject(Double.class);
			hR_CreateLocal(variables[index], Double.class);
			method.visitInsn(AASTORE);
		}
		if (!membership.render(this, 0)) {
			throw new IllegalArgumentException();
		}
		method.visitMethodInsn(INVOKESPECIAL, Type
				.getInternalName(UserDefinedInteractome.class), "<init>",
				makeSignature(null, String.class, Interactome[].class,
						Double.class));
		method.visitVarInsn(ALOAD, 0);
		method.visitVarInsn(ALOAD, 1);
		method.visitFieldInsn(PUTFIELD, name, FieldRunner, TypeRunner);
		method.visitInsn(Opcodes.RETURN);
		endMethod();
	}

	@Override
	protected void cleanupMethod() {
		throw new IllegalStateException();
	}

	public boolean createGeneMethod(AstNode expression) {
		return createMethod(expression, Gene.class);
	}

	public boolean createInteractomeMethod(AstNode expression) {
		return createMethod(expression, Interaction.class);
	}

	private boolean createMethod(AstNode expression, Class<?> clazz) {
		startMethod(ACC_PUBLIC, "calculateMembership", double.class, clazz);
		String signature = makeSignature(double.class, clazz);
		String interactomeinterface = Type.getInternalName(Interactome.class);
		method.visitVarInsn(ALOAD, 0);
		method.visitFieldInsn(GETFIELD, name, "$interactomes", Type
				.getInternalName(Interactome[].class));
		for (int index = 0; index < variables.length; index++) {
			Label objectify = new Label();
			Label end = new Label();
			method.visitInsn(DUP);
			method.visitLdcInsn(index);
			method.visitInsn(AALOAD);
			method.visitVarInsn(ALOAD, 1);
			method.visitMethodInsn(INVOKEINTERFACE, interactomeinterface,
					"calculateMembership", signature);
			method.visitInsn(DUP2);
			method.visitLdcInsn(-1.0D);
			method.visitInsn(DCMPL);
			method.visitJumpInsn(IFGT, objectify);
			method.visitInsn(POP2);
			method.visitInsn(ACONST_NULL);
			method.visitJumpInsn(GOTO, end);
			method.visitLabel(objectify);
			pOhO_PrimitiveToObject(Double.class);
			method.visitLabel(end);
			hR_CreateLocal(variables[index], Double.class);
		}
		method.visitInsn(POP);
		if (!expression.render(this, 0)) {
			return false;
		}
		Label returnmissing = new Label();
		method.visitInsn(DUP);
		method.visitJumpInsn(IFNULL, returnmissing);
		pOhO_ObjectToPrimitive(Double.class);
		method.visitInsn(DRETURN);
		method.visitLabel(returnmissing);
		method.visitInsn(POP);
		method.visitLdcInsn(-1.0D);
		method.visitInsn(DRETURN);
		endMethod();
		return true;
	}
}
