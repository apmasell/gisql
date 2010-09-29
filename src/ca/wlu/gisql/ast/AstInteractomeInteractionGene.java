package ca.wlu.gisql.ast;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.graph.Interaction;

public class AstInteractomeInteractionGene extends SyntheticAccessor {
	private static final Method gene1;

	private static final Method gene2;
	private static final Logger log = Logger
			.getLogger(AstInteractomeInteractionGene.class);
	static {
		Method gene1method = null;
		Method gene2method = null;
		try {
			gene1method = Interaction.class.getMethod("getGene1");
			gene2method = Interaction.class.getMethod("getGene2");
		} catch (NoSuchMethodException e) {
			log.error("Failed to get method.", e);
		}
		gene1 = gene1method;
		gene2 = gene2method;
	}
	private final boolean first;
	private String interactionname = null;

	protected AstInteractomeInteractionGene(String name, boolean first) {
		super(name, Type.GeneType);
		this.first = first;
	}

	@Override
	public ResolutionEnvironment getModifiedEnvironment(
			ResolutionEnvironment environment) {
		return environment;
	}

	@Override
	protected <C> boolean renderSelf(Rendering<C> program, int depth) {
		return program.lRhO(interactionname)
				&& program.g_InvokeMethod(first ? gene1 : gene2);
	}

	void setInteractionName(String interactionname) {
		this.interactionname = interactionname;
	}

}
