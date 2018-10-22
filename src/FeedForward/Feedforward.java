package FeedForward;

import Backpropagate.Backpropagate;
import General.DataManager;
import General.Gene;
import General.Layer;
import General.NeuralNetwork;
import General.Neuron;
import General.PropertyReader;

public abstract class Feedforward {
		protected DataManager data;
		protected Backpropagate backpropagate;
		public Feedforward(DataManager data){
			this.data = data;
			backpropagate = data.getBackPropagate();
		}
		public void evolveRunner() {
			EvolveSetup();		
			// runs the networks for a minute to measure their performance
			feed(true);
			//see method description
			EvolveTeardown();
		
		}
		public void backpropagateRunner(){
			BackpropagationSetup();
			data.getWriter().println("Iteration" + data.getGen());
			NeuralNetwork[] nns = data.getNetworks();
			//Scales the allowed error over time allowing a much larger starting error but capping the smallest error possible at a lower but still reasonable number.
			double scaling = backpropagate.startingErrorSetup();
			// this is where the back propagation learning step for the neural networks run.
			while(data.getTotalGlobalError() > Double.parseDouble(PropertyReader.getProperty("allowedError"))/scaling){
				BackIterationHandling();
				feed(false);	
				backpropagate.backpropagate(nns);
				data.getWriter().println("Total Global Error:" + data.getTotalGlobalError()); 
				data.getWriter().println("backpropagation complete");		
			}
			
		}
		public void feed(boolean evolve){
			NeuralNetwork[] nns = data.getNetworks();
				for (NeuralNetwork nn : nns){
					RunNetwork(nn);					
				}
				long t = System.currentTimeMillis();
				while(System.currentTimeMillis() - t < Integer.parseInt(PropertyReader.getProperty("timing")));
		}
		public void RunNetwork(NeuralNetwork nn){
				//runs each layer in order
				nn.clearInputArrays();
				for (Layer l : nn.getLayers()){			
					// gets the input data, and sends it to each connected neuron.
					for (Neuron n : l.getNeurons()){
						if(l.isInput() || (l.isOutput() && Backpropagate.Sigmoid(n.getValue()) > Double.parseDouble(PropertyReader.getProperty("activation"))))n.invoke();
						if(!l.isOutput())runGenes(n);
						n.setLast(n.getValue());
						n.setValue(0.0000001);
					}
				}		
		}
		private void runGenes(Neuron n) {
			for (Gene g : n.getGenes()){
				Neuron connect = g.getConnection();
				double weight = g.getWeight();
				double value = Backpropagate.Sigmoid(n.getValue());
				g.setLastInput(connect.getValue()+value*weight);
				connect.setValue(connect.getValue()+value*weight);
				g.getConnection().addInput(g);
				g.setInput(n);
			}
		}
		//this is called when evolve begins.
		public abstract void EvolveSetup();
		//this is called after evolve finishes
		public abstract void EvolveTeardown();
		// This is called at the beginning of each iteration of learning
		// this is for data that needs to be reset or generated on each iteration of learning
		public abstract void BackpropagationSetup();
		// This runs before the backpropagation step is called each time
		// this is for data that needs to be reset or generated on each iteration of backpropagtion
		public abstract void BackIterationHandling();

}

