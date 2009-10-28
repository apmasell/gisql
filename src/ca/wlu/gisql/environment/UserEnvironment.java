package ca.wlu.gisql.environment;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.output.FileFormat;

/** This environment is where the user should be making definitions. */
public class UserEnvironment extends Environment {
	private FileFormat format = FileFormat.summary;

	private AstLiteral last = null;

	private int numCommands = 1;

	private String output = null;

	public UserEnvironment(Environment parent) {
		super(parent, true, false);
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
		last = new AstLiteral(Type.InteractomeType, result);
		setVariable(name, last);

		return result;
	}

	public FileFormat getFormat() {
		return format;
	}

	public AstLiteral getLast() {
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
