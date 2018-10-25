package Backpropagate;
import java.util.Random;

import General.DataManager;
import General.Properties;
import General.PropertyReader;
import NeuralNetwork.Gene;
import NeuralNetwork.Layer;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.Neuron;
/**
 * Backpropagate.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
public abstract class Backpropagate {
	protected DataManager data;
	public static Random rand = new Random();
	public Backpropagate(DataManager data) {
		this.data = data;
	}
	public double startingErrorSetup() {
		double scaling = Math.log(data.getGen())*3+1;
		data.setTotalGlobalError(Double.parseDouble(PropertyReader.getProperty(Properties.ALLOWED_ERROR.toString()))/scaling + 1);
		return scaling;
	}
	public void backpropagate(NeuralNetwork[] nns) {
		//Determines which neurons should have fired
		setAct();
		for(NeuralNetwork nn : nns){					
			outputErrorCalculation(nn);
			outputGeneCorrection(nn);
			for(int i = nn.getLayers().size()-1; i > 0; i--){
				Layer l = nn.getLayers().get(i);
				if(!l.isOutput()){
					for(Neuron n: l.getNeurons()){
						hiddenErrorCalculation(n);
						geneCorrection(n);
					}
				}
			}
			globalErrorCalculation(nn);
		}		
	}
	private void geneCorrection(Neuron n) {
		for(Gene g : n.getInputs()){
			Neuron input = g.getInput();
			g.setLastChange(n.getError()*Double.parseDouble(PropertyReader.getProperty(Properties.LEARNING_RATE.toString()))*input.getLast()+g.getWeight()+g.getLastChange()*Double.parseDouble(PropertyReader.getProperty(Properties.MOMENTUM.toString())));
			g.setWeight(g.getLastChange());
		}
	}
	private void hiddenErrorCalculation(Neuron n) {
		double outputErrors = 0;
		int m = 0;
		int expected = 0;
		for(Gene g : n.getGenes()){
			m++;
			outputErrors += g.getConnection().getError();
			expected += g.getConnection().getError()/g.getWeight();
			
		}
		if(m>0)outputErrors = outputErrors/m;
		expected = expected/m;
		n.setError((expected-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*outputErrors);
	}
	private void outputGeneCorrection(NeuralNetwork nn) {
		//set weight for each output gene
		Layer out = nn.getLayers().get(nn.getLayers().size()-1);	
		for(Neuron n: out.getNeurons()){
			geneCorrection(n);
		}
	}
	private void globalErrorCalculation(NeuralNetwork nn) {
		double totalsum = 0;
		for(Layer l : nn.getLayers()){
			double sum = 0;
			if (l.isOutput())for(Neuron n : l.getNeurons())sum += n.getError();	
			else if(!l.isInput()) for(Neuron n : l.getNeurons())sum += n.getError();
			totalsum += Math.pow(sum, 2);
		}
		nn.setGlobalError(totalsum/2);
		data.setTotalGlobalError(data.getTotalGlobalError() + totalsum/2);
	}
	private static void outputErrorCalculation(NeuralNetwork nn) {
		Layer out = nn.getLayers().get(nn.getLayers().size()-1);	
		for(Neuron n : out.getNeurons()){
			double activation =Double.parseDouble(PropertyReader.getProperty(Properties.ACTIVATION.toString()));
			if(n.getInputs().size() == 0) n.setError(0);
			else if(n.shouldAct && n.getLast() < activation)n.setError((activation+Math.random()-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*(1-Sigmoid(n.getLast())));
			else if(!n.shouldAct && n.getLast() > activation)n.setError((-activation-Math.random()-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*(1-Sigmoid(n.getLast())));
			else n.setError(0);			
		}
	}
	public static double Sigmoid(double d) {
		return 1/(1+Math.exp(d*-1));
	}
	public abstract void setAct();
	
}
