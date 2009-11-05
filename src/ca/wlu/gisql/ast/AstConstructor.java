package ca.wlu.gisql.ast;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.annotation.GisqlType;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.NativeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeParser;
import ca.wlu.gisql.environment.ParserEnvironment;
import ca.wlu.gisql.vm.InstructionConstruct;
import ca.wlu.gisql.vm.Machine;

/**
 * Represents a native function that really corresponds to a Java constructor.
 * See {@link GisqlConstructorFunction} and {@link ParserEnvironment}.
 */
public final class AstConstructor extends AstNative {

	/** Use reflected Java type to determine the equivalent query language type. */
	private static Type convertType(java.lang.reflect.Type javatype) {
		if (javatype instanceof Class<?>) {
			Class<?> clazz = (Class<?>) javatype;

			for (Field field : Type.class.getFields()) {
				if (Modifier.isStatic(field.getModifiers())
						&& NativeType.class.isAssignableFrom(field.getType())) {
					NativeType matchtype;
					try {
						matchtype = (NativeType) field.get(null);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						return null;
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						return null;
					}
					if (matchtype.handlesNativeType(clazz)) {
						return matchtype;
					}
				}
			}

			throw new IllegalArgumentException("Class " + clazz.getName()
					+ " as not compatible representation");

		} else if (javatype instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType) javatype;
			Class<?> clazz = (Class<?>) ptype.getRawType();

			if (List.class.isAssignableFrom(clazz)) {
				Type contents = convertType(ptype.getActualTypeArguments()[0]);
				return new ListType(contents);
			}
			throw new IllegalArgumentException("Unknown parameterized type "
					+ ptype);
		} else {
			throw new IllegalArgumentException("Unknown argument type");
		}
	}

	/**
	 * Construct a list of type (to generate an arrow type) for this function,
	 * based on the constructor's signature.
	 */
	private static Type[] generateTypes(Constructor<?> constructor) {
		java.lang.reflect.Type[] parameters = constructor
				.getGenericParameterTypes();
		Annotation[][] annotations = constructor.getParameterAnnotations();
		boolean needsmachine = parameters[0] instanceof Class<?>
				&& Machine.class.isAssignableFrom((Class<?>) parameters[0]);

		Type[] types = new Type[parameters.length + 1 - (needsmachine ? 1 : 0)];

		for (int index = needsmachine ? 1 : 0; index < parameters.length; index++) {
			int destinationindex = index - (needsmachine ? 1 : 0);
			for (Annotation annotation : annotations[index]) {
				if (annotation instanceof GisqlType) {
					GisqlType gisqltype = (GisqlType) annotation;
					TypeParser parser = new TypeParser(gisqltype.type());
					types[destinationindex] = parser.parse();
					break;
				}
			}
			if (types[destinationindex] == null) {
				types[destinationindex] = convertType(parameters[index]);
			}
		}
		types[types.length - 1] = convertType(constructor.getDeclaringClass());
		return types;
	}

	private final Constructor<?> constructor;

	public AstConstructor(Class<?> clazz) {
		super(clazz.getAnnotation(GisqlConstructorFunction.class).name(), clazz
				.getAnnotation(GisqlConstructorFunction.class).description(),
				generateTypes(clazz.getConstructors()[0]));
		constructor = clazz.getConstructors()[0];
	}

	@Override
	public final boolean render(ProgramRoutine program, int depth, int debrujin) {
		if (depth >= types.length - 1) {
			return program.instructions.add(new InstructionConstruct(
					constructor));
		} else {
			return wrap(program, depth, debrujin);
		}
	}

}
