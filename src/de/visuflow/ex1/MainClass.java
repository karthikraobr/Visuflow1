package de.visuflow.ex1;
import java.io.File;
import java.util.Map;
import java.util.Iterator;
import de.visuflow.reporting.EmptyReporter;
import de.visuflow.reporting.IReporter;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Main;
import soot.PackManager;
import soot.Transform;
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.toolkits.scalar.ConstantPropagatorAndFolder;

public class MainClass {

	public static void main(String[] args) {
		runAnalysis(new EmptyReporter());
	}

	public static void runAnalysis(final IReporter reporter) {
		G.reset();

		Transform transform = new Transform("jtp.myTransform", new BodyTransformer() {
			@Override
			protected void internalTransform(Body b, String phase, Map<String, String> options) {
				// TODO implement the BodyTransformer here.
				ConstantPropagatorAndFolder.v().transform(b);
                soot.PatchingChain<soot.Unit> units = b.getUnits();
                for (soot.Unit unit:units
                     ) {
                    if(unit instanceof soot.jimple.InvokeStmt){
                        //System.out.print(unit.toString());
                        if(((JInvokeStmt) unit).getInvokeExpr().toString().contains("Cipher")&&((JInvokeStmt) unit).getInvokeExpr().toString().contains("getInstance") && ((JInvokeStmt) unit).getInvokeExpr().toString().contains("DES")){
                            reporter.report(b.getMethod(),unit);
                        }
                    }
                }
            }
		});

		PackManager.v().getPack("jtp").add(transform);

		String rtJar = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";

		// Run Soot
		Main.main(new String[] { "-cp", "./bin" + File.pathSeparator + rtJar, "-exclude", "javax",
				"-allow-phantom-refs", "-no-bodies-for-excluded", "-process-dir", "./targetBin1", "-src-prec",
				"only-class", "-output-format", "none", "de.visuflow.analyzeMe.ex1.TargetClass1" });
	}

}
