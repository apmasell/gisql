package ca.wlu.gisql.ast.util;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class EnumGenerator implements Opcodes {

	@SuppressWarnings("unchecked")
	public static <E extends Enum<?>> Class<E> create(String name,
			List<String> values) {
		String enumname = "GisqlEnum" + name;
		String enumdescriptor = "L" + enumname + ";";
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		writer.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER + ACC_ENUM,
				enumname, "Ljava/lang/Enum<" + enumdescriptor + ">;",
				"java/lang/Enum", new String[] {});

		for (String value : values) {
			writer.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC + ACC_ENUM,
					value, enumdescriptor, null, null).visitEnd();
		}

		writer.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC + ACC_SYNTHETIC,
				"ENUM$VALUES", "[" + enumdescriptor, null, null).visitEnd();

		MethodVisitor clinitmethod = writer.visitMethod(ACC_STATIC, "<clinit>",
				"()V", null, null);
		clinitmethod.visitCode();
		int index = 0;
		for (String value : values) {
			clinitmethod.visitTypeInsn(NEW, enumname);
			clinitmethod.visitInsn(DUP);
			clinitmethod.visitLdcInsn(value);
			clinitmethod.visitIntInsn(BIPUSH, index++);
			clinitmethod.visitMethodInsn(INVOKESPECIAL, enumname, "<init>",
					"(Ljava/lang/String;I)V");
			clinitmethod.visitFieldInsn(PUTSTATIC, enumname, value,
					enumdescriptor);
		}
		clinitmethod.visitIntInsn(BIPUSH, values.size());
		clinitmethod.visitTypeInsn(ANEWARRAY, enumname);
		index = 0;
		for (String value : values) {
			clinitmethod.visitInsn(DUP);
			clinitmethod.visitIntInsn(BIPUSH, index++);
			clinitmethod.visitFieldInsn(GETSTATIC, enumname, value,
					enumdescriptor);
			clinitmethod.visitInsn(AASTORE);
		}
		clinitmethod.visitFieldInsn(PUTSTATIC, enumname, "ENUM$VALUES", "["
				+ enumdescriptor);
		clinitmethod.visitInsn(RETURN);
		clinitmethod.visitMaxs(0, 0);
		clinitmethod.visitEnd();
		MethodVisitor initmethod = writer.visitMethod(ACC_PRIVATE, "<init>",
				"(Ljava/lang/String;I)V", null, null);
		initmethod.visitCode();
		initmethod.visitVarInsn(ALOAD, 0);
		initmethod.visitVarInsn(ALOAD, 1);
		initmethod.visitVarInsn(ILOAD, 2);
		initmethod.visitMethodInsn(INVOKESPECIAL, "java/lang/Enum", "<init>",
				"(Ljava/lang/String;I)V");
		initmethod.visitInsn(RETURN);
		initmethod.visitMaxs(0, 0);
		initmethod.visitEnd();

		MethodVisitor valuesmethod = writer.visitMethod(
				ACC_PUBLIC + ACC_STATIC, "values", "()[" + enumdescriptor,
				null, null);
		valuesmethod.visitCode();
		valuesmethod.visitFieldInsn(GETSTATIC, enumname, "ENUM$VALUES", "["
				+ enumdescriptor);
		valuesmethod.visitInsn(DUP);
		valuesmethod.visitInsn(ARRAYLENGTH);
		valuesmethod.visitTypeInsn(ANEWARRAY, enumname);
		valuesmethod.visitInsn(DUP_X1);
		valuesmethod.visitInsn(ICONST_0);
		valuesmethod.visitInsn(SWAP);
		valuesmethod.visitInsn(DUP);
		valuesmethod.visitInsn(ARRAYLENGTH);
		valuesmethod.visitInsn(ICONST_0);
		valuesmethod.visitInsn(SWAP);
		valuesmethod.visitMethodInsn(INVOKESTATIC, "java/lang/System",
				"arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
		valuesmethod.visitInsn(ARETURN);
		valuesmethod.visitMaxs(0, 0);
		valuesmethod.visitEnd();
		MethodVisitor valueofmethod;

		valueofmethod = writer.visitMethod(ACC_PUBLIC + ACC_STATIC, "valueOf",
				"(Ljava/lang/String;)" + enumdescriptor, null, null);
		valueofmethod.visitCode();
		valueofmethod.visitLdcInsn(Type.getType(enumdescriptor));
		valueofmethod.visitVarInsn(ALOAD, 0);
		valueofmethod.visitMethodInsn(INVOKESTATIC, "java/lang/Enum",
				"valueOf",
				"(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;");
		valueofmethod.visitTypeInsn(CHECKCAST, enumname);
		valueofmethod.visitInsn(ARETURN);
		valueofmethod.visitMaxs(0, 0);
		valueofmethod.visitEnd();

		MethodVisitor nextmethod = writer.visitMethod(ACC_PUBLIC + ACC_BRIDGE
				+ ACC_SYNTHETIC, "next", "()Ljava/lang/Enum;", null, null);
		nextmethod.visitCode();
		nextmethod.visitVarInsn(ALOAD, 0);
		nextmethod.visitMethodInsn(INVOKEVIRTUAL, enumname, "next", "()"
				+ enumdescriptor);
		nextmethod.visitInsn(ARETURN);
		nextmethod.visitMaxs(0, 0);
		nextmethod.visitEnd();
		writer.visitEnd();

		return (Class<E>) ClassCreator.<E> load(enumname, writer);
	}
}
