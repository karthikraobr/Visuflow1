package de.visuflow.analyzeMe.ex2;

public class TargetClass2 {
	
	private String whatsoever;
	
	private void leak(String data) {
		System.out.println("Leak: " + data);
	}
	public void sourceToSink() {
		String x = getSecret();
		String y = x;
		leak(y);
	}
	
	
	private String getSecret() {
		return "top secret";
	}
	

}
