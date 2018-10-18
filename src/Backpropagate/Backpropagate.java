package Backpropagate;
import java.util.Random;

import Competitive.Competition;
import General.DataManager;
import General.Gene;
import General.Layer;
import General.MethodManager;
import General.NeuralNetManager;
import General.NeuralNetwork;
import General.Neuron;
import General.PropertyReader;
/**
 * Backpropagate.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
public class Backpropagate {
	
	public static Random rand = new Random();
	public static void runner(DataManager data){
		MethodManager netManager = netManagerReflected(data);
		netManager.BackpropagationSetup(data);
		data.getWriter().println("Iteration" + data.getGen());
		NeuralNetwork[] nns = data.getNetworks();
		//Scales the allowed error over time allowing a much larger starting error but capping the smallest error possible at a lower but still reasonable number.
		double scaling = startingErrorSetup(data);
		// see method description
		for(NeuralNetwork nn : nns) {
			NeuralNetManager.Neuraltracker(nn);	
		}
		//Different types of network running for competitive networks
		
		// this is where the back propagation learning step for the neural networks run. currently I have them set to run for one minute before evaluating
		while(data.getTotalGlobalError() > Double.parseDouble(PropertyReader.getProperty("allowedError"))/scaling){
			if(Integer.parseInt(PropertyReader.getProperty("competing")) > 1){
				Competition.backpropagationRunner(data);
			}
			else{
				//set necessary values for backpropagation step
				netManager.BackIterationHandling(data);
				//run the networks
				for (NeuralNetwork nn : nns){
					NeuralNetManager.RunNetwork(nn);					
				}
				long t = System.currentTimeMillis();
				while(System.currentTimeMillis() - t < Integer.parseInt(PropertyReader.getProperty("timing")));
				backpropagate(nns,data);
			}	
			data.getWriter().println("Total Global Error:" + data.getTotalGlobalError()); 
			data.getWriter().println("backpropagation complete");		
		}
		
	}
	private static double startingErrorSetup(DataManager data) {
		double scaling = Math.log(data.getGen())*3+1;
		data.setTotalGlobalError(Double.parseDouble(PropertyReader.getProperty("allowedError"))/scaling + 1);
		return scaling;
	}
	private static MethodManager netManagerReflected(DataManager data){	
		return data.getMethods();
	}
	public static void backpropagate(NeuralNetwork[] nns, DataManager data) {
		MethodManager netManager = netManagerReflected(data);
		//Determines which neurons should have fired
		netManager.setAct(data);
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
			globalErrorCalculation(data, nn);
		}		
	}
	private static void geneCorrection(Neuron n) {
		for(Gene g : n.getInputs()){
			Neuron input = g.getInput();
			g.setLastChange(n.getError()*Double.parseDouble(PropertyReader.getProperty("learningRate"))*input.getLast()+g.getWeight()+g.getLastChange()*Double.parseDouble(PropertyReader.getProperty("momentum")));
			g.setWeight(g.getLastChange());
		}
	}
	private static void hiddenErrorCalculation(Neuron n) {
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
	private static void outputGeneCorrection(NeuralNetwork nn) {
		//set weight for each output gene
		Layer out = nn.getLayers().get(nn.getLayers().size()-1);	
		for(Neuron n: out.getNeurons()){
			geneCorrection(n);
		}
	}
	private static void globalErrorCalculation(DataManager data, NeuralNetwork nn) {
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
			double activation =Double.parseDouble(PropertyReader.getProperty("activation"));
			if(n.getInputs().size() == 0) n.setError(0);
			else if(n.shouldAct && n.getLast() < activation)n.setError((activation+Math.random()-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*(1-Sigmoid(n.getLast())));
			else if(!n.shouldAct && n.getLast() > activation)n.setError((-activation-Math.random()-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*(1-Sigmoid(n.getLast())));
			else n.setError(0);			
		}
	}
	public static double Sigmoid(double d) {
		return 1/(1+Math.exp(d*-1));
	}
	public static void BackIterationHandling(DataManager data){
		MethodManager netManager = netManagerReflected(data);
		netManager.BackIterationHandling(data);		
	}
	
}
