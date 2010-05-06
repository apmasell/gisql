package ca.wlu.gisql.ast;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.annotation.GisqlType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeParser;
import ca.wlu.gisql.ast.util.BuiltInResolver;
import ca.wlu.gisql.ast.util.Rendering;

/**
 * Represents a native function that really corresponds to a Java constructor.
 * See {@link GisqlConstructorFunction} and {@link BuiltInResolver}.
 */
public final class AstNativeConstructor extends AstNative {

	/**
	 * Construct a list of type (to generate an arrow type) for this function,
	 * based on the constructor's signature.
	 */
	private static Type[] generateTypes(Constructor<?> constructor) {
		java.lang.reflect.Type[] parameters = constructor
				.getGenericParameterTypes();
		Annotation[][] annotations = constructor.getParameterAnnotations();

		Type[] types = new Type[parameters.length + 1];

		for (int index = 0; index < parameters.length; index++) {
			for (Annotation annotation : annotations[index]) {
				if (annotation instanceof GisqlType) {
					GisqlType gisqltype = (GisqlType) annotation;
					TypeParser parser = new TypeParser(gisqltype.type());
					types[index] = parser.parse();
					break;
				}
			}
			if (types[index] == null) {
				types[index] = Type.convertType(parameters[index]);
			}
		}
		types[types.length - 1] = Type.convertType(constructor
				.getDeclaringClass());
		return types;
	}

	private final Constructor<?> constructor;

	public AstNativeConstructor(Class<?> clazz) {
		super(clazz.getAnnotation(GisqlConstructorFunction.class).name(), clazz
				.getAnnotation(GisqlConstructorFunction.class).description(),
				generateTypes(clazz.getConstructors()[0]));
		constructor = clazz.getConstructors()[0];
	}

	@Override
	public final <T> boolean renderSelf(Rendering<T> program, int depth) {
		return program.pRg$hO_CreateObject(constructor);
	}

}
