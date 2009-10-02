package ca.wlu.gisql.vm;

class Frame {

	static final Frame java = new Frame(null);

	int counter = 0;
	final Program program;

	Frame(Program subroutine) {
		super();
		program = subroutine;
	}
}
