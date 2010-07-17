package ca.wlu.gisql.ast;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.annotation.GisqlType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.BuiltInResolver;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.parser.TypeKnowledgeBase;

/**
 * Represents a native function that really corresponds to a Java constructor.
 * See {@link GisqlConstructorFunction} and {@link BuiltInResolver}.
 */
public final class AstNativeConstructor extends AstNative {

	private static Type convertType(Annotation[] annotations,
			java.lang.reflect.Type parameter, TypeKnowledgeBase kb) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof GisqlType) {
				GisqlType gisqltype = (GisqlType) annotation;
				Type type = Parser.parseType(kb, gisqltype.type());
				if (type == null) {
					throw new IllegalArgumentException("Malformed type: "
							+ gisqltype.type());
				} else {
					return type;
				}
			}
		}
		Type type = Type.convertType(parameter);
		if (type == null) {
			throw new IllegalArgumentException(
					"Unable to determine type from Java type: " + parameter);
		} else {
			return type;
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
		TypeKnowledgeBase kb = new TypeKnowledgeBase();

		Type[] types = new Type[parameters.length + 1];

		for (int index = 0; index < parameters.length; index++) {
			types[index] = convertType(annotations[index], parameters[index],
					kb);
		}
		types[types.length - 1] = convertType(constructor.getAnnotations(),
				constructor.getDeclaringClass(), kb);
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
