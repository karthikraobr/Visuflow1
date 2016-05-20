package de.visuflow.ex2;

import java.util.HashSet;
import java.util.Set;

import de.visuflow.reporting.IReporter;
import soot.*;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JReturnStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class IntraproceduralAnalysis extends ForwardFlowAnalysis<Unit, Set<FlowAbstraction>> {
	public int flowThroughCount = 0;
	private final SootMethod method;
	private final IReporter reporter;

	public IntraproceduralAnalysis(Body b, IReporter reporter) {
		super(new ExceptionalUnitGraph(b));
		this.method = b.getMethod();
		this.reporter = reporter;
	}

	@Override
	protected void flowThrough(Set<FlowAbstraction> in, Unit d, Set<FlowAbstraction> out) {
		Stmt s = (Stmt) d;

		//Variable to check if taintsIn needs to be added to out
		Boolean keepTaint = false;

		//Conditional Analysis for Assignment Statements
		if(s instanceof JAssignStmt)
		{
			//Check if assignment is to getSecret function
			if(s.toString().contains("getSecret"))
			{
				if(!s.getDefBoxes().isEmpty()){
					for(ValueBox defBox:s.getDefBoxes()){
						//Adding tainted variable to out to obtain it as taintsIn during next iteration
						out.add(getFlowAbstractionObj(s,defBox.getValue()));
						//System.out.println("Intial Taint "+ defBox.getValue());
					}
				}
			}else{

				for(FlowAbstraction i : in){
					for(ValueBox useBox:s.getUseBoxes()){
						if(i.getLocal().equals(useBox.getValue())){
							for(ValueBox defBox:s.getDefBoxes()){
								if(s.containsFieldRef())
								{
									reporter.report(this.method, i.getSource(), d);
								}else{
									out.add(getFlowAbstractionObj(s,defBox.getValue()));
									//System.out.println(defBox.getValue() + " is tainted because of "+useBox.getValue());
								}
							}

						}else{
							keepTaint = true;
						}
					}

				}
				if(!in.isEmpty())
				{
					keepTaint = true;
				}

			}
		}
		//Conditional Analysis for Function Calls and Return Statements
		else if ((s instanceof JInvokeStmt && !s.toString().contains("getSecret")) || s instanceof JReturnStmt){
			for(FlowAbstraction i : in)
			{
				for(ValueBox useBox:s.getUseBoxes()){
					if(i.getLocal().equals(useBox.getValue())){
						//System.out.println(useBox.getValue() + " is Leaking Out!");
						reporter.report(this.method, i.getSource(), d);
					}

					else{
						keepTaint = true;
					}
				}

			}
		}
		if(keepTaint){
			out.addAll(in);
		}
	}

	@Override
	protected Set<FlowAbstraction> newInitialFlow() {
		return new HashSet<FlowAbstraction>();
	}

	@Override
	protected Set<FlowAbstraction> entryInitialFlow() {
		return new HashSet<FlowAbstraction>();
	}

	@Override
	protected void merge(Set<FlowAbstraction> in1, Set<FlowAbstraction> in2, Set<FlowAbstraction> out) {
		out.addAll(in1);
		out.addAll(in2);
	}

	@Override
	protected void copy(Set<FlowAbstraction> source, Set<FlowAbstraction> dest) {
		dest.clear();
		dest.addAll(source);
	}

	public void doAnalyis() {
		super.doAnalysis();
	}
	private FlowAbstraction getFlowAbstractionObj(Stmt s, Value value)
	{
		return new FlowAbstraction(s, (Local)value);
	}
}

