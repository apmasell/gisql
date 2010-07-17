package ca.wlu.gisql.environment;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.EmptyInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.output.FileFormat;

/** This environment is where the user should be making definitions. */
public class UserEnvironment extends Environment {
	private static final Logger log = Logger.getLogger(UserEnvironment.class);

	private FileFormat format = FileFormat.summary;

	private Interactome last = null;

	private int numCommands = 1;

	private String output = null;

	public UserEnvironment(Environment parent) {
		super(parent, true, false);
		add("last", EmptyInteractome.self, Type.InteractomeType);
	}

	public <E extends Enum<?>> Type add(String name, Class<E> enumeration) {
		Type type = Type.convertType(enumeration);
		if (type == null) {
			type = Type.installType(name, enumeration);
		}
		for (E item : enumeration.getEnumConstants()) {
			setVariable(item.toString(), item, type);
		}
		return type;
	}

	/**
	 * Appending an interactome will create a history-numbered version of the
	 * interactome in this environment.
	 */
	public CachedInteractome append(Interactome interactome) {
		if (interactome == null) {
			return null;
		}
		String name = "_" + numCommands++;
		CachedInteractome result = CachedInteractome.wrap(interactome, name);
		last = result;
		add(name, result, Type.InteractomeType);

		return result;
	}

	public FileFormat getFormat() {
		return format;
	}

	public Interactome getLast() {
		return last;
	}

	public String getOutput() {
		return output;
	}

	public void setFormat(FileFormat format) {
		this.format = format;
	}

	public void setOutput(String output) {
		this.output = output;
	}
}
