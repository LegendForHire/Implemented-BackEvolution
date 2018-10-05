package Backpropagate;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import Competitive.Competition;
import General.Gene;
import General.Layer;
import General.NeuralNetManager;
import General.NeuralNetwork;
import General.Neuron;
import General.PropertyReader;
import General.Singleton;
import General.SpecialNetManager;
/**
 * Backpropagate.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
public class Backpropagate {
	
	public static Random rand = new Random();
	public static void runner(Singleton s) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, ClassNotFoundException, SecurityException, InstantiationException, InterruptedException {
		SpecialNetManager netManager = netManagerReflected(s);
		netManager.BackpropagationSetup();
		s.getWriter().println("Iteration" + s.getGen());
		NeuralNetwork[] nns = s.getNetworks();
		//Scales the allowed error over time allowing a much larger starting error but capping the smallest error possible at a lower but still reasonable number.
		double scaling = startingErrorSetup(s);
		// see method description
		for(NeuralNetwork nn : nns) {
			NeuralNetManager.Neuraltracker(nn);	
		}
		//Different types of network running for competitive networks
		
		// this is where the back propagation learning step for the neural networks run. currently I have them set to run for one minute before evaluating
		while(s.getTotalGlobalError() > Double.parseDouble(PropertyReader.getProperty("allowedError"))/scaling){
			if(Integer.parseInt(PropertyReader.getProperty("competing")) > 1){
				Competition.backpropagationRunner(s);
			}
			else{
				//set necessary values for backpropagation step
				netManager.BackIterationHandling();
				//run the networks
				for (NeuralNetwork nn : nns){
					NeuralNetManager.RunNetwork(nn,s);					
				}
				long t = System.currentTimeMillis();
				while(System.currentTimeMillis() - t < Integer.parseInt(PropertyReader.getProperty("timing")));
				backpropagate(nns,s);
			}	
			s.getWriter().println("Total Global Error:" + s.getTotalGlobalError()); 
			s.getWriter().println("backpropagation complete");		
		}
		
	}
	private static double startingErrorSetup(Singleton s) {
		double scaling = Math.log(s.getGen())*3+1;
		s.setTotalGlobalError(Double.parseDouble(PropertyReader.getProperty("allowedError"))/scaling + 1);
		return scaling;
	}
	@SuppressWarnings("unchecked")
	private static SpecialNetManager netManagerReflected(Singleton s)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {	
		String type = PropertyReader.getProperty("type");
		Class<? extends SpecialNetManager> class1 = (Class<? extends SpecialNetManager>) Class.forName("BackEvolution."+type+"."+type+"NetManager");
		@SuppressWarnings("deprecation")
		SpecialNetManager netManager = class1.newInstance();
		return netManager;
	}
	public static void backpropagate(NeuralNetwork[] nns, Singleton s) throws IOException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException {
		SpecialNetManager netManager = netManagerReflected(s);
		//Determines which neurons should have fired
		netManager.setAct();
		for(NeuralNetwork nn : nns){					
			outputErrorCalculation(s, nn);
			outputGeneCorrection(s, nn);
			for(int i = nn.getLayers().size()-1; i > 0; i--){
				Layer l = nn.getLayers().get(i);
				if(!l.isOutput()){
					for(Neuron n: l.getNeurons()){
						hiddenErrorCalculation(n);
						geneCorrection(s, n);
					}
				}
			}
			globalErrorCalculation(s, nn);
		}		
	}
	private static void geneCorrection(Singleton s, Neuron n) {
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
	private static void outputGeneCorrection(Singleton s, NeuralNetwork nn) {
		//set weight for each output gene
		Layer out = nn.getLayers().get(nn.getLayers().size()-1);	
		for(Neuron n: out.getNeurons()){
			geneCorrection(s, n);
		}
	}
	private static void globalErrorCalculation(Singleton s, NeuralNetwork nn) {
		double totalsum = 0;
		for(Layer l : nn.getLayers()){
			double sum = 0;
			if (l.isOutput())for(Neuron n : l.getNeurons())sum += n.getError();	
			else if(!l.isInput()) for(Neuron n : l.getNeurons())sum += n.getError();
			totalsum += Math.pow(sum, 2);
		}
		nn.setGlobalError(totalsum/2);
		s.setTotalGlobalError(s.getTotalGlobalError() + totalsum/2);
	}
	private static void outputErrorCalculation(Singleton s, NeuralNetwork nn) {
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
	public static void BackIterationHandling(Singleton s) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String type = PropertyReader.getProperty("type");
		@SuppressWarnings("unchecked")
		Class<? extends SpecialNetManager> class1 = (Class<? extends SpecialNetManager>) Class.forName("BackEvolution."+type+"."+type+"NetManager");
		@SuppressWarnings("deprecation")
		SpecialNetManager netManager = class1.newInstance();
		netManager.BackIterationHandling();		
	}
	
}
