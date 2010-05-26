package ca.wlu.gisql.ast.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.runner.ExpressionRunner;

/**
 * Provides a mechanism to render abstract syntax in to Java bytecode and load
 * the resulting class into the current JVM.
 * 
 * Methods in the class follow an obscure naming convention. Each method has a
 * list of actions and places where these actions occur. There are certain,
 * confusingly, parallel structure between runtime and compile time. The actions
 * are: <b>c</b>opy, <b>p</b>op, pus<b>h</b>, <b>s</b>tore, <b>l</b>oad, and
 * <b>g</b>enerate code. The know places are the run-time <b>O</b>perand stack,
 * the run-time <b>V</b>ariable stack, the runtime <b>E</b>nvironment, the
 * runtime ExpressionRu<b>N</b>ner, the compile-time <b>P</b>arameter stack (the
 * counterpart of the operand stack), the compile-time <b>R</b>eference stack
 * (the counterpart of the variable stack), and a constant (<b>X</b>). The
 * suffix <b>$</b> indicates that an action may be repeated.
 */
public abstract class Rendering<T> implements Opcodes {

	/** Represents a variable passed as an argument (in a box). */
	protected class ArgumentVariable implements Variable, Renderable {
		private final int offset;
		private final String type;

		public ArgumentVariable(int offset, String type) {
			super();
			this.offset = offset;
			this.type = type;
		}

		@Override
		public boolean finish() {
			return true;
		}

		@Override
		public String getName() {
			return "$" + offset;
		}

		@Override
		public boolean load() {
			method.visitVarInsn(ALOAD, 1);
			method.visitLdcInsn(offset);
			method.visitInsn(AALOAD);
			method.visitTypeInsn(CHECKCAST, type);
			return true;
		}

		@Override
		public <X> boolean render(Rendering<X> rendering, int depth) {
			if (rendering != Rendering.this) {
				throw new IllegalArgumentException(
						"Out of context use of a variable.");
			}
			return load();
		}

		@Override
		public <X> boolean store(Rendering<X> source) {
			throw new IllegalStateException("Arguments cannot be stored.");
		}

		@Override
		public String toString() {
			return offset + "[A]";
		}

	}

	/** Casts the result of rendering to a specific class. */
	public static class Cast implements Renderable {

		private final Class<?> clazz;
		private final Renderable value;

		public Cast(Renderable value, Class<?> clazz) {
			super();
			this.value = value;
			this.clazz = clazz;
		}

		@Override
		public <T> boolean render(Rendering<T> rendering, int depth) {
			if (value.render(rendering, depth)) {
				rendering.method.visitTypeInsn(CHECKCAST, Type
						.getInternalName(clazz));
				return true;
			} else {
				return false;
			}
		}

	}

	/** The dynamic class loader. */
	private static class ClassCreator extends ClassLoader {
		@SuppressWarnings("unchecked")
		<T> Class<? extends T> load(String name, ClassWriter writer) {
			byte[] bytecode = writer.toByteArray();
			try {
				Class<? extends T> clazz = (Class<? extends T>) defineClass(
						name, bytecode, 0, bytecode.length);
				if (clazz.getConstructors().length == 1) {
					if (System.getProperty("gisql.debug", "false").equals(
							"true")) {
						saveClassToFile(name, bytecode, null);
					}
					return clazz;
				} else {
					saveClassToFile(name, bytecode, null);
				}
			} catch (VerifyError e) {
				saveClassToFile(name, bytecode, e);
			}
			return null;
		}

		private void saveClassToFile(String name, byte[] bytecode, Throwable t) {
			String file = System.getProperty("java.io.tmpdir") + File.separator
					+ name + ".class";
			if (t == null) {
				log.warn("Saving byte code to " + file + ".");
			} else {
				log.error(
						"Failed to generate valid byte code. Saving bad byte code to "
								+ file + " for analysis.", t);
			}
			try {
				FileOutputStream fos;
				fos = new FileOutputStream(file);
				fos.write(bytecode);
				fos.close();
			} catch (IOException x) {
				log.error("Failed to save class.", x);
			}
		}
	}

	/**
	 * A variable free in the current context, which has been stored, by the
	 * caller, in a field.
	 */
	protected class ExternalVariable implements Variable {
		private final FieldVisitor field;
		private final String name;
		private final String type;

		public ExternalVariable(String name, String type) {
			this.name = name;
			this.type = type;
			field = writer.visitField(ACC_PUBLIC, name, Type
					.getDescriptor(Object.class), null, null);
			field.visitEnd();
		}

		@Override
		public boolean finish() {
			return true;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean load() {
			method.visitVarInsn(ALOAD, 0);
			method.visitFieldInsn(GETFIELD, Rendering.this.name, name, Type
					.getDescriptor(Object.class));
			method.visitTypeInsn(CHECKCAST, type);
			return true;
		}

		@Override
		public <X> boolean store(Rendering<X> source) {
			source.method.visitInsn(SWAP);
			source.method.visitInsn(DUP_X1);
			source.method.visitInsn(SWAP);
			source.method.visitFieldInsn(PUTFIELD, Rendering.this.name, name,
					Type.getDescriptor(Object.class));
			return true;
		}

		@Override
		public String toString() {
			return name + "[E]";
		}

	}

	/**
	 * A reference to the current continuation. Effectively, a reference to
	 * <tt>this</tt>.
	 */
	protected class SelfReference implements Variable {

		private final String name;

		public SelfReference(String name) {
			this.name = name;
		}

		@Override
		public boolean finish() {
			return true;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean load() {
			method.visitVarInsn(ALOAD, 0);
			return true;
		}

		@Override
		public <X> boolean store(Rendering<X> source) {
			throw new IllegalStateException("Functions cannot be stored.");
		}

		@Override
		public String toString() {
			return name + "[T]";
		}

	}

	/** A variable stored on the variable stack. Effectively, a local variable. */
	protected class StackVariable implements Variable {
		private final int index;

		private final String name;
		private Label start = null;
		private final String type;

		public StackVariable(String name, Class<?> type) {
			super();
			this.name = name;
			this.type = Type.getInternalName(type);
			index = ++Rendering.this.index;
		}

		public StackVariable(int index, String name, Class<?> type) {
			super();
			this.index = index;
			this.name = name;
			this.type = Type.getInternalName(type);
		}

		@Override
		public boolean finish() {
			Label end = new Label();
			method.visitLabel(end);
			method.visitLocalVariable(name, Type.getDescriptor(Object.class),
					null, start, end, index);
			return true;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean load() {
			method.visitVarInsn(ALOAD, index);
			return true;
		}

		@Override
		public <X> boolean store(Rendering<X> source) {
			if (source != Rendering.this) {
				throw new IllegalArgumentException(
						"Local variables can only be set from local context.");
			}
			start = new Label();
			method.visitLabel(start);
			method.visitTypeInsn(CHECKCAST, type);
			method.visitVarInsn(ASTORE, index);
			return true;
		}

		@Override
		public String toString() {
			return name + "[S:" + index + "]";
		}
	}

	/**
	 * The interface for variable-like things. That is, things which have a name
	 * that can be made to produce a value on the operand stack.
	 */
	protected interface Variable {

		/** Do any cleanup when this variable has fallen out of use. */
		boolean finish();

		String getName();

		/** Put contents on the operand stack. */
		boolean load();

		/**
		 * Move contents from the operand stack into storage.
		 * 
		 * @param source
		 *            the place in which the code to do this will be generated.
		 */
		<T> boolean store(Rendering<T> source);
	}

	private static final ClassCreator creator = AccessController
			.doPrivileged(new PrivilegedAction<ClassCreator>() {
				public ClassCreator run() {
					return new ClassCreator();
				}
			});

	protected static final String FieldRunner = "$runner";

	protected static final Logger log = Logger.getLogger(Rendering.class);

	private static final Map<Class<?>, Class<?>> primitives = new HashMap<Class<?>, Class<?>>();

	protected final static String TypeRunner = Type
			.getDescriptor(ExpressionRunner.class);

	static {
		primitives.put(Byte.class, byte.class);
		primitives.put(Short.class, short.class);
		primitives.put(Integer.class, int.class);
		primitives.put(Long.class, long.class);
		primitives.put(Float.class, float.class);
		primitives.put(Double.class, double.class);
		primitives.put(Boolean.class, boolean.class);
		primitives.put(Character.class, char.class);
	}

	public static Class<?> convertPrimitive(Class<?> type) {
		for (Entry<Class<?>, Class<?>> entry : primitives.entrySet()) {
			if (entry.getValue() == type) {
				return entry.getKey();
			}
		}
		return null;
	}

	private int index = 0;

	protected MethodVisitor method;

	protected final String name;

	protected final Stack<Renderable> parameters = new Stack<Renderable>();

	protected final Stack<Variable> references = new Stack<Variable>();

	private final ClassWriter writer;

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
	protected Rendering(String name, Class<T> clazz) {
		this.name = name + hashCode();

		/* Create a new class extending GenericFunction to fill. */
		writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		writer.visit(V1_6, ACC_PUBLIC, this.name, null,
				clazz.isInterface() ? "java/lang/Object" : Type
						.getInternalName(clazz),
				clazz.isInterface() ? new String[] { Type
						.getInternalName(clazz) } : null);

		makeField(FieldRunner, TypeRunner);
	}

	protected abstract void cleanupMethod();

	protected final void endMethod() {
		method.visitEnd();
		method.visitMaxs(0, 0);
		method = null;
	}

	public boolean g_Cast(Class<?> type) {
		method.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
		return true;
	}

	/**
	 * Calls a raw instruction. This assumes you know how you are manipulating
	 * the object stack.
	 */
	public boolean g_Insn(int insn) {
		method.visitInsn(insn);
		return true;
	}

	public boolean g_InstanceOf(Class<?> type) {
		method.visitInsn(DUP);
		method.visitTypeInsn(INSTANCEOF, Type.getInternalName(type));
		return true;
	}

	/**
	 * Generate code to invoke a method given the arguments are already on the
	 * operand stack.
	 */
	public boolean g_InvokeMethod(Method reflectedmethod) {
		int opcode = Modifier.isStatic(reflectedmethod.getModifiers()) ? INVOKESTATIC
				: reflectedmethod.getDeclaringClass().isInterface() ? INVOKEINTERFACE
						: INVOKEVIRTUAL;
		String declaringclass = Type.getInternalName(reflectedmethod
				.getDeclaringClass());
		String descriptor = Type.getMethodDescriptor(reflectedmethod);
		method.visitMethodInsn(opcode, declaringclass, reflectedmethod
				.getName(), descriptor);
		return true;
	}

	/**
	 * Given a list of renderable items, generate code to create a list and push
	 * it onto the operand stack.
	 */
	public boolean g$hO_CreateList(List<? extends Renderable> list) {
		try {
			pRg$hO_CreateObject(ArrayList.class.getConstructor());
		} catch (SecurityException e) {
			return false;
		} catch (NoSuchMethodException e) {
			return false;
		}

		for (Renderable node : list) {
			method.visitInsn(DUP);
			if (!node.render(this, 0)) {
				return false;
			}
			method.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add",
					"(Ljava/lang/Object;)Z");
			method.visitInsn(POP);
		}
		return true;
	}

	/** Finish code generation and load the resulting class. */
	public Class<? extends T> generate() {
		if (method != null) {
			cleanupMethod();
			endMethod();
		}
		return creator.load(name, writer);

	}

	private Variable getReferenceByName(String name) {
		for (int index = references.size() - 1; index >= 0; index--) {
			if (references.get(index).getName().equals(name)) {
				return references.get(index);
			}
		}
		return null;
	}

	public boolean gF$_CreateFields(List<VariableInformation> variables) {
		Set<String> names = new HashSet<String>();

		for (int index = variables.size() - 1; index >= 0; index--) {
			if (!names.contains(variables.get(index).getName())) {
				names.add(variables.get(index).getName());

				references.push(new ExternalVariable(variables.get(index)
						.getName(), variables.get(index).getInternalName()));
			}
		}
		return true;
	}

	/**
	 * For the the free variables provided, copy them from the current
	 * continuation into a newly created continuation.
	 * 
	 * @param source
	 *            the calling continuation
	 * @param variablenames
	 *            the names of the variables as they appear in the calling
	 *            continuation. Names will be copied into the destination
	 *            continuation.
	 */
	public boolean gF$_lVhF$_CopyVariablesFromParent(Rendering<?> source,
			List<VariableInformation> variables) {
		Set<String> names = new HashSet<String>();

		for (int index = variables.size() - 1; index >= 0; index--) {
			if (!names.contains(variables.get(index).getName())) {
				names.add(variables.get(index).getName());
				if (!(source.lRhO(variables.get(index).getName()) && getReferenceByName(
						variables.get(index).getName()).store(source))) {
					return false;
				}
			}
		}
		return true;
	}

	/** Determine if there is currently a variable known by this name. */
	public boolean hasReference(String name) {
		for (Variable variable : references) {
			if (variable.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Push a constant onto the operand stack. This constant must be a primitive
	 * or a String.
	 */
	public boolean hO(Object value) {
		method.visitLdcInsn(value);
		return true;
	}

	/**
	 * Pushes a primitive constant on the stack and then wraps it as an object.
	 */
	public boolean hO_AsObject(Object value) {
		if (value == null) {
			method.visitInsn(ACONST_NULL);
			return true;
		} else {
			method.visitLdcInsn(value);
			return pOhO_PrimitiveToObject(value.getClass());
		}
	}

	/**
	 * Finish a subroutine by generating it and then creating a reference to it
	 * in the current continuation.
	 */
	public <X> boolean hO_CreateSubroutine(Rendering<X> subroutine) {
		Class<? extends X> clazz = subroutine.generate();
		return pRg$hO_CreateObject(clazz.getConstructors()[0]);
	}

	public boolean hP(final Object value) {
		return hP(new Renderable() {

			@Override
			public <X> boolean render(Rendering<X> rendering, int depth) {
				return rendering.hO(value);
			}
		});
	}

	/**
	 * Add a parameter to the current compile-time parameter stack. This should
	 * be paried with {@link #pPg()}.
	 */
	public boolean hP(Renderable parameter) {
		return parameters.push(parameter) != null;
	}

	public boolean hP_AsObject(final Object value) {
		return hP(new Renderable() {

			@Override
			public <X> boolean render(Rendering<X> rendering, int depth) {
				return rendering.hO_AsObject(value);
			}
		});
	}

	/** Create a new local variable with a specific name. */
	public boolean hR_CreateLocal(String name, Class<?> type) {
		Variable variable = new StackVariable(name, type);
		references.push(variable);
		return variable.store(this);
	}

	/** Create a new variable that is a reference to <tt>this</tt>. */
	public boolean hR_CreateSelfReference(String name) {
		references.push(new SelfReference(name));
		return true;
	}

	/** Perform any of the Java jump/if. */
	public boolean jump(int opcode, Label label) {
		method.visitJumpInsn(opcode, label);
		return true;
	}

	/** Place the current environment on the operand stack. */
	public boolean lEhO() {
		lNhO();
		try {
			g_InvokeMethod(ExpressionRunner.class.getMethod("getEnvironment"));
			return true;
		} catch (SecurityException e) {
			log.error("ExpressionRunner.getEnvironment problem", e);
		} catch (NoSuchMethodException e) {
			log.error("ExpressionRunner.getEnvironment problem", e);
		}
		return false;
	}

	/**
	 * Get a specific variable from the environment and place the result on the
	 * operand stack.
	 */
	public boolean lEhO(String name) {
		lEhO();
		method.visitLdcInsn(name);
		try {
			g_InvokeMethod(UserEnvironment.class.getMethod("getVariable",
					String.class));
		} catch (SecurityException e) {
			log.error("Environment.getVariable problem", e);
			return false;
		} catch (NoSuchMethodException e) {
			log.error("Environment.getVariable problem", e);
			return false;
		}
		return true;
	}

	/**
	 * Load the contents of a field. If the field is not static, the object
	 * hosting the field must be on the stack.
	 */
	public boolean lFhO(Field field) {
		method.visitFieldInsn(
				Modifier.isStatic(field.getModifiers()) ? GETSTATIC : GETFIELD,
				Type.getInternalName(field.getDeclaringClass()), field
						.getName(), Type.getDescriptor(field.getType()));
		return true;
	}

	/** Load the field {@link Unit#nil} onto the stack. */
	public boolean lFhO_Nil() {
		try {
			return lFhO(Unit.class.getField("nil"));
		} catch (SecurityException e) {
			log.error("Couldn't access Unit.nil field.", e);
		} catch (NoSuchFieldException e) {
			log.error("Couldn't access Unit.nil field.", e);
		}
		return false;
	}

	/** Put the current runner on the operand stack. */
	public boolean lNhO() {
		method.visitVarInsn(ALOAD, 0);
		method.visitFieldInsn(GETFIELD, name, FieldRunner, TypeRunner);
		return true;
	}

	/** Duplicate the top of the operand stack. */
	public boolean lOhO() {
		method.visitInsn(DUP);
		return true;
	}

	/** Load the value of a variable on to the operand stack. */
	public boolean lRhO(String name) {
		Variable reference = getReferenceByName(name);
		return reference != null && reference.load();
	}

	protected void makeField(String name, String type) {
		FieldVisitor fv = writer.visitField(ACC_PRIVATE + ACC_FINAL, name,
				type, null, null);
		fv.visitEnd();

	}

	protected String makeSignature(Class<?> returntype,
			Class<?>... parametertypes) {
		StringBuilder signature = new StringBuilder();
		signature.append('(');
		for (Class<?> clazz : parametertypes) {
			signature.append(Type.getDescriptor(clazz));
		}
		signature.append(')');
		if (returntype == null) {
			signature.append('V');
		} else {
			signature.append(Type.getDescriptor(returntype));
		}
		return signature.toString();
	}

	/** Label the current point in the code for jumping purposes. */
	public boolean mark(Label label) {
		method.visitLabel(label);
		return true;
	}

	/** Pop an item off the operand stack. */
	public boolean pO() {
		method.visitInsn(POP);
		return true;
	}

	/** Convert the top of the operand stack from an object to a primitive. */
	public boolean pOhO_ObjectToPrimitive(Class<?> clazz) {
		method.visitTypeInsn(CHECKCAST, Type.getInternalName(clazz));
		method.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(clazz),
				clazz.getSimpleName().toLowerCase() + "Value", "()"
						+ Type.getDescriptor(primitives.get(clazz)));
		return true;
	}

	/** Convert the top of the operand stack from an object to a primitive. */
	public boolean pOhO_PrimitiveToObject(Class<?> clazz) {
		if (clazz != String.class) {
			method.visitMethodInsn(INVOKESTATIC, Type.getInternalName(clazz),
					"valueOf", "(" + Type.getDescriptor(primitives.get(clazz))
							+ ")" + Type.getDescriptor(clazz));
		}
		return true;
	}

	/**
	 * Store the top of the operand stack in the environment. The
	 * success/failure result is discarded.
	 */
	public boolean pOsE(String name, ca.wlu.gisql.ast.type.Type type) {
		lEhO();
		method.visitInsn(SWAP);
		method.visitLdcInsn(name);
		method.visitInsn(SWAP);
		type.render(this, 0);
		try {
			g_InvokeMethod(UserEnvironment.class.getMethod("setVariable",
					String.class, Object.class,
					ca.wlu.gisql.ast.type.Type.class));
		} catch (SecurityException e) {
			log.error("Could not access environment set method.", e);
			return false;
		} catch (NoSuchMethodException e) {
			log.error("Could not access environment set method.", e);
			return false;
		}
		method.visitInsn(POP);
		return true;
	}

	/** Discard a parameter. */
	public Renderable pP() {
		return parameters.pop();
	}

	/**
	 * Pop a single parameter from the stack and generate code. The code should
	 * place the result on the operand stack.
	 */
	public boolean pPg() {
		return parameters.pop().render(this, 0);
	}

	/**
	 * Remove the specified number of parameters from the parameter stack,
	 * generate code for them, and package them as an array, which is left on
	 * the operand stack.
	 */
	public boolean pPg$hO_BoxArguments(int count) {
		return pPg$hO_BoxArguments(count, null);
	}

	/**
	 * Remove the specified number of parameters from the parameter stack,
	 * generate code for them, running some routine to fix them up, and package
	 * them as an array, which is left on the operand stack.
	 */
	public boolean pPg$hO_BoxArguments(int count, Renderable fix) {
		method.visitLdcInsn(count);
		method.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		for (int index = 0; index < count; index++) {
			method.visitInsn(DUP);
			method.visitLdcInsn(index);
			if (!(pPg() && (fix == null || fix.render(this, 0)))) {
				return false;
			}
			method.visitInsn(AASTORE);
		}
		return true;
	}

	/**
	 * Pop a variable. Although variables must be discarded in LIFO order, the
	 * name must be provided to ensure correctness.
	 */
	public boolean pR(String name) {
		Variable variable = references.pop();
		if (variable.getName().equals(name)) {
			return variable.finish();
		} else {
			throw new IllegalStateException("Corrupt stack while popping "
					+ name + " from " + references);
		}
	}

	/**
	 * Load parameters off the parameter stack and feed them to a Java
	 * constructor, leaving the result object on the operand stack. If the
	 * constructor takes {@link ExpressionRunner} as the first parameter, this
	 * will be provided automatically.
	 */
	public boolean pRg$hO_CreateObject(Constructor<?> constructor) {
		String classname = Type
				.getInternalName(constructor.getDeclaringClass());

		method.visitTypeInsn(NEW, classname);
		method.visitInsn(DUP);

		for (Class<?> clazz : constructor.getParameterTypes()) {
			if (ExpressionRunner.class.isAssignableFrom(clazz)) {

				if (!lNhO()) {
					return false;
				}
			} else {
				if (!pPg()) {
					return false;
				}
				if (clazz != Object.class && !clazz.isPrimitive()) {
					method
							.visitTypeInsn(CHECKCAST, Type
									.getInternalName(clazz));
				}
			}
		}

		method.visitMethodInsn(INVOKESPECIAL, classname, "<init>",
				makeSignature(null, constructor.getParameterTypes()));
		return true;
	}

	protected final void startMethod(int modifiers, String name,
			Class<?> returntype, Class<?>... parametertypes) {
		if (method != null) {
			throw new IllegalStateException(
					"Can only render one method at a time.");
		}

		method = writer.visitMethod(modifiers, name, makeSignature(returntype,
				parametertypes), null, null);
		method.visitCode();

		index = parametertypes.length;
		while (references.size() > 0
				&& !(references.peek() instanceof Rendering<?>.ExternalVariable)) {
			references.pop();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class: ").append(name);
		sb.append(" parameters: ").append(parameters);
		sb.append(" references: ").append(references);
		return sb.toString();
	}
}
