package General;

import java.io.PrintWriter;

/**
 * Singleton.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */

public interface Singleton {
	//create fields for all items that have getters and setter
	//create constants for all methods with only getters. 
	//these are your twiddly knobs for changing how the algorithm runs a bit.
	// also when implementing create the getInstance method of the singleton
	// and at least an empty constructor
	public double getTotalGlobalError();
	public void setTotalGlobalError(double totalGlobalError);
	public PrintWriter getWriter();
	public void setWriter(PrintWriter w);
	public void setNetworks(NeuralNetwork[] nns);
	public NeuralNetwork[] getNetworks();
	public int getTiming();
	public double getActivation();
	public String getType();
	public void incrementGen();
	public int getGen();
	public int getNumNetworks();
	public double getLearningRate();
	public double getMomentum();
	//This is the allowed error across your networks. backpropagation step will keep running until this is satisfied
	double getAllowedError();
	//set this return to 1 or lower if not competing
	public int numCompeting();
	//if returns 0 Backpropagation, if returns 1 both, if returns 2 Evolution.
	public int getLearningType();
}

