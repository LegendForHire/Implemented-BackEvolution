package BackEvolution;
/**
 * NeuralNetwork.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public abstract class NeuralNetwork implements Comparable<NeuralNetwork>{
	protected ArrayList<Layer> layers;
	protected double fitness;
	private int age;
	private long created;
	private double globalError;
	public NeuralNetwork(Layer inputLayer, Layer outputLayer, Class<?> a) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		layers = new ArrayList<Layer>();
		Class<?>[] types = {a};
		Constructor<?> con =  a.getConstructor(types);
		layers.add((Layer) con.newInstance(a.cast(inputLayer)));
		layers.get(0).setNumber(1);	
		layers.add((Layer) con.newInstance(a.cast(outputLayer)));
		layers.get(1).setNumber(2);
		fitness = 0;
		//loads the currencies from the bittrex api.		
		age = 0;
		setCreated(System.currentTimeMillis());
	}
	//made to clone neural networks. didn't work as intended, but not deleted in case used elsewhere for other purpose.
	public NeuralNetwork(NeuralNetwork nn, Class<?> a) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ArrayList<Layer> layers = nn.getLayers();
		for (Layer l : layers){
			this.layers.add((Layer) a.getConstructor(a.getClass()).newInstance(a.cast(l)));
		}
		age = 0;	
	}
	

	public void addLayer(Layer layer){
		layers.add(layers.size()-1, layer);
		layers.get(layers.size()-2).setNumber(layers.size()-1);
		layers.get(layers.size()-1).setNumber(layers.size());
	}
	public ArrayList<Layer> getLayers(){
		return layers;
	}
	public double getFitness(){
		return fitness;
	}
	//resets the wallets to a default state
	
	//finds the fitness in amount of bitcoin.
	public void updateFitness() throws IOException {
		System.out.println("Override Fitness method in NeuralNetwork Implementation");
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	@Override
	public int compareTo(NeuralNetwork n) {
        if (n == null) return -1;
		if(this.getFitness()>n.getFitness()){
			return -1;
		}
		if(this.getFitness()==n.getFitness()){
			int nn1 = 0;
			int nn2 = 0;
			for(Layer l : n.getLayers()){
				if(!l.isOutput()){
					for(Neuron neuron : l.getNeurons()){
						nn1 += neuron.getGenes().size();
					}
				}
			}
			for(Layer l : this.getLayers()){
				if(!l.isOutput()){
					for(Neuron neuron : l.getNeurons()){
						nn2 += neuron.getGenes().size();
					}
				}
			}
			if(nn1>nn2)return 1;
			if(nn2>nn1)return -1;
			return 0;
		}
		return 1;
	}
	public long getCreated() {
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public void setGlobalError(double totalsum) {
		// TODO Auto-generated method stub
		globalError = totalsum;
	}
	public double getGlobalError(){
		return globalError;
	}
	public void clearInputArrays() {
		for(Layer l : layers){
			for(Neuron n : l.getNeurons()){
				n.clearInputs();
			}
		}
		
	}
	
}
