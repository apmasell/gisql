package ca.wlu.gisql.ast;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import ca.wlu.gisql.ast.type.RecordType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.SimpleRecord;

/**
 * Representation of a record construction. Records are constructed by creating
 * a new record, possibly copying the contents of an existing record, and then
 * adding new fields to the record.
 */
public class AstRecordNew extends AstNode {

	private final AstNode origin;
	private final Map<String, AstNode> parameters = new TreeMap<String, AstNode>();
	private final RecordType type = new RecordType();

	public AstRecordNew(AstNode origin) {
		this.origin = origin;
	}

	public void add(String name, AstNode node) {
		parameters.put(name, node);
		type.add(name, node.getType());
	}

	@Override
	protected void freeVariables(Set<String> variables) {
		for (AstNode node : parameters.values()) {
			node.freeVariables(variables);
		}
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	protected <T> boolean renderSelf(Rendering<T> program, int depth) {
		return program.g$hO_CreateMap(SimpleRecord.class, origin, parameters);
	}

	@Override
	public void resetType() {
		type.reset();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		AstNode origin = null;
		if (this.origin != null) {
			origin = this.origin.resolve(runner, context, environment);
			if (origin == null) {
				return null;
			}
		}
		AstRecordNew node = new AstRecordNew(origin);
		for (Entry<String, AstNode> entry : parameters.entrySet()) {
			AstNode parameter = entry.getValue().resolve(runner, context,
					environment);
			if (parameter == null) {
				return null;
			} else {
				node.add(entry.getKey(), parameter);
			}
		}
		return node;
	}

	@Override
	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("< ");
		boolean first = origin == null;
		if (origin != null) {
			print.print(origin);
		}
		for (Entry<String, AstNode> entry : parameters.entrySet()) {
			if (first) {
				first = false;
			} else {
				print.print(" ; ");
			}
			print.print(entry.getKey());
			print.print(" = ");
			print.print(entry.getValue());

		}
		print.print(" >");
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		if (origin != null) {
			if (!origin.type(runner, context)) {
				return false;
			}
			RecordType origintype = new RecordType(true);
			if (!origin.getType().unify(origintype)) {
				runner.appendTypeError(origin.getType(), type, this, context);
				return false;
			}
			type.putAll(origintype);
		}
		for (AstNode node : parameters.values()) {
			if (!node.type(runner, context)) {
				return false;
			}
		}
		return true;
	}

}
