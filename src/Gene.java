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
	private OutputNeuron connectO;
	//multiplier, 0 if disabled, 1 if enabled. enabled by default at creation
	private int enabled;
	private Neuron input;
	private InputNeuron inputI;
	private double lastInput;
	private double lastChange;
	public Gene(Neuron connected, double weight){
		connect = connected;
		connectweight = weight;
		enabled = 1;
	}

	public Gene(OutputNeuron connected, double weight){
		connectO = connected;
		connectweight = weight;
		enabled = 1;
	}
	//returns the weight if enabled
	public double getWeight(){
		return connectweight*enabled;
	}
	//returns the connection
	public Neuron getConnection(){
		if (connect == null)return connectO;
		return connect;
	}
	//this is usually called if i know it has an output neuron as a connection.
	public OutputNeuron getOConnection(){
		if (connect == null)return connectO;
		return (OutputNeuron) connect;
	}
	//changes the weight of the neuron
	public void setWeight (double w){
		connectweight = w;
	}
	// sets a connection
	public void setConnection(Neuron n) {
		connect = n;
		
	}
	// toggles between enabled and disabled
	public void toggle() {
		if (enabled == 1) enabled = 0;
		else enabled = 1;		
	}
	//returns 0 or 1 based on enabled or diabled.
	public int getstate(){
		return enabled;
	}

	public Neuron getInput() {
		return input;
	}

	public void setInput(Neuron input) {
		this.input = input;
	}

	public InputNeuron getInputI() {
		return inputI;
	}

	public void setInputI(InputNeuron inputI) {
		this.inputI = inputI;
	}

	public double getLastInput() {
		return lastInput;
	}

	public void setLastInput(double lastInput) {
		this.lastInput = lastInput;
	}

	public double getLastChange() {
		// TODO Auto-generated method stub
		return lastChange;
	}

	public void setLastChange(double d) {
		// TODO Auto-generated method stub
		lastChange=d;
	}
}
