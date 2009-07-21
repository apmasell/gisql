package ca.wlu.gisql.functions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstVoid;
import ca.wlu.gisql.environment.parser.util.Function;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class PhylogenyGenerator extends Function {

	private class PhylogGen extends AstVoid {
		private final String filename;
		private final AstList parameters;

		public PhylogGen(AstList list, String filename) {
			parameters = list;
			this.filename = filename;
		}

		public void execute() {
			List<Tree> interactomes = new ArrayList<Tree>();
			for (Interactome source : parameters.asInteractomeList()) {
				CachedInteractome interactome = CachedInteractome.wrap(source,
						null);
				if (!interactome.process()) {
					log.error("Error computing " + interactome.toString());
				}
				interactomes.add(new Leaf(interactome));
			}

			ScoringMatrix<Tree> old = null;
			while (interactomes.size() > 1) {
				log.info("Computing scoring matrix...");
				ScoringMatrix<Tree> matrix = new ScoringMatrix<Tree>(
						interactomes, ModifiedJaccardScore.self, old);

				log.warn("Merging " + matrix.getMinimumLeft().toString()
						+ " and " + matrix.getMinimumRight().toString());
				Branch branch = new Branch(matrix.getMinimumLeft(), matrix
						.getMinimumRight(), matrix.getMinimum());
				interactomes.remove(matrix.getMinimumLeft());
				interactomes.remove(matrix.getMinimumRight());
				interactomes.add(branch);

				log.warn("Cleaning up...");
				old = matrix;
				System.gc();
			}

			try {
				ShowablePrintWriter<Object> print;
				print = (filename == null ? new ShowablePrintWriter<Object>(
						System.out, interactomes)
						: new ShowablePrintWriter<Object>(new FileOutputStream(
								filename, true), interactomes));
				print.print(interactomes.get(0));
				print.println(";");
				if (filename != null)
					print.close();
			} catch (FileNotFoundException e) {
				log.error("Cannot save output.", e);
			}

		}

	}

	public static Function descriptor = new PhylogenyGenerator();

	private static final Logger log = Logger
			.getLogger(PhylogenyGenerator.class);

	private PhylogenyGenerator() {
		super("phylog", new Function.Parameter[] { new Function.ListExpression(
				"list") });
	}

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		AstList list = (AstList) params.get(0);
		if (list.size() > 0) {
			String filename = null;
			if (environment instanceof UserEnvironment)
				filename = ((UserEnvironment) environment).getOutput();
			return new PhylogGen(list, filename);
		} else
			return null;
	}

}
