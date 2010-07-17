package ca.wlu.gisql.ast.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassWriter;

import ca.wlu.gisql.GisQL;

/** The dynamic class loader. */
final class ClassCreator extends ClassLoader {
	private static final ClassCreator creator = AccessController
			.doPrivileged(new PrivilegedAction<ClassCreator>() {
				public ClassCreator run() {
					return new ClassCreator();
				}
			});

	private static final Logger log = Logger.getLogger(ClassCreator.class);

	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T> load(String name, ClassWriter writer) {
		byte[] bytecode = writer.toByteArray();
		try {
			Class<? extends T> clazz = (Class<? extends T>) creator
					.defineClass(name, bytecode, 0, bytecode.length);
			if (clazz.getConstructors() != null) {
				if (GisQL.debug) {
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

	private static void saveClassToFile(String name, byte[] bytecode,
			Throwable t) {
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

	private ClassCreator() {
		super();
	}
}