package BackEvolution;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
/**
 * Backpropagate.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
public class Backpropagate {
	
	public static Random rand = new Random();
	@SuppressWarnings("deprecation")
	public static void backpropagate(NeuralNetwork[] nns, Singleton s) throws IOException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException {
		s.setTotalGlobalError(0.0);
		@SuppressWarnings("unchecked")
		Class<? extends SpecialNetManager> class1 = (Class<? extends SpecialNetManager>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"NetManager");
		SpecialNetManager netManager = class1.newInstance();
		netManager.setAct();
		for(NeuralNetwork nn : nns){
			Layer out = nn.getLayers().get(nn.getLayers().size()-1);			
			// calculate the error for each output neuron
			for(Neuron n : out.getNeurons()){
				if(n.getInputs().size() == 0) n.setError(0);
				else if(n.shouldAct && n.getLast() < s.getActivation())n.setError((1.01+Math.random()-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*(1-Sigmoid(n.getLast())));
				else if(!n.shouldAct && n.getLast() > s.getActivation())n.setError((-.01+Math.random()-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*(1-Sigmoid(n.getLast())));
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
						//calculate the error for Neurons
						double outputErrors = 0;
						for(Gene g : n.getGenes()){
							outputErrors = g.getConnection().getError();							
						}
						double expected = 1; // I need to figure out how to figure out the target, was not in documentation I read. Seems its supposed to be 1 but not sure;	t
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
	public static double Sigmoid(double d) {
		return 1/(1+Math.exp(d*-1));
	}

}
