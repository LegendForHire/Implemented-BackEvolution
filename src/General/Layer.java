package General;
import java.lang.reflect.InvocationTargetException;
/**
 * Layer.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
import java.util.ArrayList;

public abstract class Layer {
	private ArrayList<Neuron> neurons;
	private boolean input;
	private boolean output;
	private int number;
	public Layer(boolean isInput, boolean isOutput){
		output = isOutput;
		input = isInput;
		neurons = new ArrayList<Neuron>();
	}
	public Layer(Neuron neuron,boolean isInput, boolean isOutput){
		output = isOutput;
		input = isInput;
		neurons = new ArrayList<Neuron>();
		neuron.setNumber(1);
		neuron.setLayernumber(number);
		neurons.add(neuron);
	}
	public Layer(Layer l, Class<?> a) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		neurons = new ArrayList<Neuron>();
		for (Neuron n: l.getNeurons()){
			Neuron n2 = (Neuron) a.getConstructor(a).newInstance(a.cast(n));
			neurons.add(n2);
			n2.setNumber(neurons.size());
			n2.setLayernumber(number);
		}	
		output = l.isOutput();
		input = l.isInput();
		number = l.getNumber();
	}
	public void addNeuron(Neuron n){
		neurons.add(n);
		neurons.get(neurons.size()-1).setNumber(neurons.size());
	}
	public ArrayList<Neuron> getNeurons(){
		return neurons;
	}
	public boolean isInput(){
		return input;
	}
	public boolean isOutput(){
		return output;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
		for(Neuron n : neurons)n.setLayernumber(number);
	}
	public void setOutput(boolean b) {
		output = b;		
	}
	public void setInput(boolean b) {
		input = b;
		
	}
}
