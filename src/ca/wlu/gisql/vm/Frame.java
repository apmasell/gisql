package ca.wlu.gisql.vm;

/** Hold a call stack return frame for the {@link Machine}. */
class Frame {

	/**
	 * This is a special return frame that causes the {@link Machine} to return
	 * to the invoking Java code.
	 */
	static final Frame java = new Frame(null);

	int counter = 0;
	final Program program;

	Frame(Program subroutine) {
		super();
		program = subroutine;
	}
}
