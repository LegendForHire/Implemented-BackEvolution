package Backpropagate;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import Competitive.Competition;
import Competitive.CompetitionSingleton;
import General.Gene;
import General.Layer;
import General.NeuralNetManager;
import General.NeuralNetwork;
import General.Neuron;
import General.Singleton;
/**
 * Backpropagate.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
public class Backpropagate {
	
	public static Random rand = new Random();
	public static void runner(Singleton s1) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, ClassNotFoundException, SecurityException, InstantiationException, InterruptedException {
		BackpropagateSingleton s = (BackpropagateSingleton) s1;
		@SuppressWarnings("deprecation")
		BackpropagateManager netManager = netManagerReflected(s);
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
		while(s.getTotalGlobalError() > s.getAllowedError()/scaling){
			if(s.numCompeting() > 1){
				Competition.backpropagationRunner((CompetitionSingleton) s);
			}
			else{
				//set necessary values for backpropagation step
				netManager.BackIterationHandling();
				//run the networks
				for (NeuralNetwork nn : nns){
					NeuralNetManager.RunNetwork(nn,s);					
				}
				long t = System.currentTimeMillis();
				while(System.currentTimeMillis() - t < s.getTiming())
				backpropagate(nns,s);
			}	
			s.getWriter().println("Total Global Error:" + s.getTotalGlobalError()); 
			s.getWriter().println("backpropagation complete");		
		}
		
	}

	private static double startingErrorSetup(BackpropagateSingleton s) {
		double scaling = Math.log(s.getGen())*3+1;
		s.setTotalGlobalError(s.getAllowedError()/scaling + 1);
		return scaling;
	}

	private static BackpropagateManager netManagerReflected(BackpropagateSingleton s)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		Class<? extends BackpropagateManager> class1 = (Class<? extends BackpropagateManager>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"NetManager");
		@SuppressWarnings("deprecation")
		BackpropagateManager netManager = class1.newInstance();
		return netManager;
	}

	@SuppressWarnings("deprecation")
	public static void backpropagate(NeuralNetwork[] nns, Singleton s1) throws IOException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException {
		BackpropagateSingleton s = (BackpropagateSingleton) s1;
		BackpropagateManager netManager = netManagerReflected(s);
		//Determines which neurons should have fired
		netManager.setAct();
		for(NeuralNetwork nn : nns){
			Layer out = nn.getLayers().get(nn.getLayers().size()-1);			
			// calculate the error for each output neuron
			for(Neuron n : out.getNeurons()){
				if(n.getInputs().size() == 0) n.setError(0);
				else if(n.shouldAct && n.getLast() < s.getActivation())n.setError((.8+Math.random()-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*(1-Sigmoid(n.getLast())));
				else if(!n.shouldAct && n.getLast() > s.getActivation())n.setError((-.8-Math.random()-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*(1-Sigmoid(n.getLast())));
				else n.setError(0);			
			}
			for(int i = nn.getLayers().size()-1; i > 0; i--){
				Layer l = nn.getLayers().get(i);
				if(l.isOutput()){
					//set weight for each output gene
					for(Neuron n: l.getNeurons()){
						for(Gene g : n.getInputs()){
							Neuron input = g.getInput();							
							g.setLastChange(n.getError()*s.getLearningRate()*input.getLast()+g.getWeight()+g.getLastChange()*s.getMomentum());
							g.setWeight(g.getLastChange());							
						}
					}
				}
				else{
					for(Neuron n: l.getNeurons()){
						//calculate the error for hidden layer Neurons
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
						//adjust the weight for genes
						for(Gene g : n.getInputs()){
							Neuron input = g.getInput();
								g.setLastChange(n.getError()*s.getLearningRate()*input.getLast()+g.getWeight()+g.getLastChange()*s.getMomentum());
								g.setWeight(g.getLastChange());
						}
					}
				}
			}
			//calculate Global Error
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
	}
	//regularly used sigmoid function
	public static double Sigmoid(double d) {
		return 1/(1+Math.exp(d*-1));
	}

	public static void BackIterationHandling(Singleton s) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		@SuppressWarnings("unchecked")
		Class<? extends BackpropagateManager> class1 = (Class<? extends BackpropagateManager>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"NetManager");
		@SuppressWarnings("deprecation")
		BackpropagateManager netManager = class1.newInstance();
		netManager.BackIterationHandling();		
	}
	
}
