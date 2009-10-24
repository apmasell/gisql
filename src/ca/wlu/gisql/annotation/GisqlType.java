package ca.wlu.gisql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ca.wlu.gisql.vm.Program;

/**
 * Overrides the Java type of a parameter into query language type. This is
 * necessary for {@link Program} types, and membership types.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface GisqlType {
	String type();
}
