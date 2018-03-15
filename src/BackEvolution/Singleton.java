package BackEvolution;

import java.io.PrintWriter;

/**
 * Singleton.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */

public interface Singleton {
	public double getAllowed();
	public void setAllowed(double a);
	public double getTotalGlobalError();
	public void setTotalGlobalError(double totalGlobalError);
	public PrintWriter getWriter();
	public void setWriter(PrintWriter w);
	public void setNetworks(NeuralNetwork[] nns);
	public NeuralNetwork[] getNetworks();
	public int getTiming();
	public void setTiming(int i);
	public double getActivation();
	public void setActivation(double d);
	public String getType();
	public void incrementGen();
	public int getGen();
	public void setNumNetworks(int numNetworks);
	public int getNumNetworks();
	public void setLearningRate(double learningRate);
	public double getLearningRate();
	public double getMomentum();
	public void setMomentum(double momentum);
}

