package de.visuflow.ex2;

import java.util.Map;

import de.visuflow.reporting.EmptyReporter;
import de.visuflow.reporting.IReporter;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Main;
import soot.PackManager;
import soot.Transform;

public class MainClass {

	public static void main(String[] args) {
		runAnalysis(new EmptyReporter(), 3);
	}

	public static void runAnalysis(final IReporter reporter, final int exercisenumber) {
		G.reset();

		// Register the transform
		Transform transform = new Transform("jtp.analysis", new BodyTransformer() {

			@Override
			protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
				IntraproceduralAnalysis ipa = new IntraproceduralAnalysis(b, reporter);
				ipa.doAnalyis();
			}

		});
		PackManager.v().getPack("jtp").add(transform);

		// Run Soot
		Main.main(
				new String[] { "-pp", "-process-dir", "./targetBin2", "-src-prec", "class", "-output-format", "none" });
	}

}
