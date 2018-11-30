package NeuralNetwork;

import General.DataManager;

/**
 * Gene.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
public class Gene {
	//what the neuron connects to
	private Neuron connect;
	//the weight of the connection
	private double connectweight;
	//if it connects to an output neuron uses this instead
	private Neuron input;
	private double lastInput;
	private double lastChange;
	private long geneID;
	public Gene(Neuron connected, double weight,DataManager data){
		connect = connected;
		connectweight = weight;
		geneID = data.getNewID();
		connected.addInput(this);
	}
	public Gene(Neuron connected, double weight, long id) {
		connect = connected;
		connectweight = weight;
		geneID = id;
		connected.addInput(this);
	}
	//returns the weight if enabled
	public double getWeight(){
		return connectweight;
	}
	//returns the connection
	public Neuron getConnection(){
		return connect;
	}
	//changes the weight of the neuron
	public void setWeight (double w){
		connectweight = w;
	}
	// sets a connection
	public void setConnection(Neuron n) {
		connect = n;
		
	}

	public Neuron getInput() {
		return input;
	}

	public void setInput(Neuron input) {
		this.input = input;
	}
	public double getLastInput() {
		return lastInput;
	}

	public void setLastInput(double lastInput) {
		this.lastInput = lastInput;
	}

	public double getLastChange() {
		return lastChange;
	}

	public void setLastChange(double d) {
		lastChange=d;
	}
	public long getID() {
		return geneID;
	}
	public void remove() {
		for(int i=0; i<input.genes.size();i++) {
			if(input.genes.get(i)==this) {
				input.genes.remove(i);
			}
		}
	}
}
