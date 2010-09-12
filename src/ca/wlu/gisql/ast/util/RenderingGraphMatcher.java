package ca.wlu.gisql.ast.util;

import java.awt.Point;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.SubgraphMatcher;
import ca.wlu.gisql.runner.ExpressionRunner;

public class RenderingGraphMatcher extends Rendering<SubgraphMatcher> {

	private static final String SupgraphMatcherName = Type
			.getInternalName(SubgraphMatcher.class);
	private final String[] nodes;

	public RenderingGraphMatcher(String[] nodes, List<Point> connected,
			List<Point> disconnected, String descriptor) {
		super("GisqlGraphMatcher", SubgraphMatcher.class);
		this.nodes = nodes;

		startMethod(ACC_PUBLIC, "<init>", null, ExpressionRunner.class);
		method.visitVarInsn(ALOAD, 0);
		method.visitLdcInsn(descriptor);
		method.visitLdcInsn(nodes.length);
		method.visitMethodInsn(INVOKESPECIAL, SupgraphMatcherName, "<init>",
				makeSignature(null, String.class, int.class));
		method.visitVarInsn(ALOAD, 0);
		method.visitVarInsn(ALOAD, 1);
		method.visitFieldInsn(PUTFIELD, name, FieldRunner, TypeRunner);

		makeConnections("connect", connected);
		makeConnections("disconnect", disconnected);

		method.visitInsn(Opcodes.RETURN);
		endMethod();
	}

	@Override
	protected void cleanupMethod() {
		throw new IllegalStateException();
	}

	private boolean createMethod(String methodname, Renderable expression,
			Class<?> returntype) {
		startMethod(ACC_PROTECTED, methodname, returntype, Gene[].class);
		for (int index = 0; index < nodes.length; index++) {
			method.visitVarInsn(ALOAD, 1);
			method.visitLdcInsn(index);
			method.visitInsn(AALOAD);
			if (!hR_CreateLocal(nodes[index], Gene.class)) {
				return false;
			}
		}
		if (!expression.render(this, 0)) {
			return false;
		}
		method.visitTypeInsn(CHECKCAST, Type.getInternalName(returntype));
		method.visitInsn(ARETURN);
		endMethod();
		return true;
	}

	public boolean createReturnMethod(Renderable expression) {
		return createMethod("compute", expression, Object.class);
	}

	public boolean createWhereMethod(Renderable expression) {
		return createMethod("isValid", expression, Boolean.class);
	}

	private void makeConnections(String methodname, List<Point> list) {
		for (Point point : list) {
			method.visitVarInsn(ALOAD, 0);
			method.visitLdcInsn(point.x);
			method.visitLdcInsn(point.y);
			method.visitMethodInsn(INVOKEVIRTUAL, SupgraphMatcherName,
					methodname, makeSignature(null, int.class, int.class));
		}

	}

}
