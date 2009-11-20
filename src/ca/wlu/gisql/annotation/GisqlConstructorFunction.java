package ca.wlu.gisql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ca.wlu.gisql.ast.util.BuiltInResolver;

/**
 * This annotation marks a class whose constructor should be made available as a
 * function in the query language. The function must be added to the
 * {@link BuiltInResolver}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GisqlConstructorFunction {
	String description();

	String name();
}
