package de.visuflow.ex1;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.List;
import de.visuflow.reporting.EmptyReporter;
import de.visuflow.reporting.IReporter;
import soot.*;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.toolkits.annotation.methods.UnreachableMethodsTagger;
import soot.jimple.toolkits.scalar.ConstantPropagatorAndFolder;

public class ReachableMainClass {

	public static void main(String[] args) {
		runAnalysis(new EmptyReporter());
	}

	public static void runAnalysis(final IReporter reporter) {
		G.reset();

		Transform transform = new Transform("wjtp.myTransform", new SceneTransformer() {
			@Override
			protected void internalTransform(String phase, Map<String, String> arg1) {

				// TODO implement the SceneTransformer here which finds only
				// reachable misuse
                soot.Body activeBody= Scene.v().getMainMethod().getActiveBody();
				PatchingChain<soot.Unit> units = activeBody.getUnits();
				for (soot.Unit unit:
				units) {

                    if (unit instanceof soot.jimple.InvokeStmt && ((JInvokeStmt) unit).getInvokeExpr().getMethod().hasActiveBody()) {
                        soot.SootMethod method = ((JInvokeStmt) unit).getInvokeExpr().getMethod();
                        ConstantPropagatorAndFolder.v().transform(method.getActiveBody());
                        soot.PatchingChain<soot.Unit> internalUnits = (method.getActiveBody().getUnits());
                        for (soot.Unit internalUnit :
                                internalUnits) {
                            if (internalUnit instanceof soot.jimple.InvokeStmt
                                    && internalUnit.toString().contains("Cipher")
                                    && internalUnit.toString().contains("getInstance")
                                    && internalUnit.toString().contains("DES")
                                    && Scene.v().getReachableMethods().contains(((JInvokeStmt) internalUnit).getInvokeExpr().getMethod())) {
                                reporter.report(((JInvokeStmt) unit).getInvokeExpr().getMethod(), internalUnit);
                            }
                        }

                    }
                }

			}
		});

		PackManager.v().getPack("wjtp").add(transform);

		String rtJar = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";

		// Run Soot
		Main.main(new String[] { "-cp", "./bin" + File.pathSeparator + rtJar, "-exclude", "javax",
				"-allow-phantom-refs", "-no-bodies-for-excluded", "-process-dir", "./targetBin1", "-src-prec",
				"only-class", "-w", "-output-format", "none", "de.visuflow.analyzeMe.ex1.TargetClass1" });
	}

}
