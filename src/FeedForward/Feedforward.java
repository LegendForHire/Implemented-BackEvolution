package FeedForward;

import Backpropagate.Backpropagate;
import Competitive.CompetitionManager;
import General.DataManager;
import General.Gene;
import General.Layer;
import General.NeuralNetwork;
import General.Neuron;
import General.PropertyReader;

public class Feedforward {
	public static void feed(boolean evolve, DataManager data, NeuralNetwork[] nns){
		if(Integer.parseInt(PropertyReader.getProperty("competing")) > 1){
			competitionRunner(data,evolve);

		}
		else{
			for (NeuralNetwork nn : nns){
				RunNetwork(nn);					
			}
			long t = System.currentTimeMillis();
			while(System.currentTimeMillis() - t < Integer.parseInt(PropertyReader.getProperty("timing")));
		}
	}
	//This is where a single neural network is run
		public static void RunNetwork(NeuralNetwork nn){
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
		private static void runGenes(Neuron n) {
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
		@SuppressWarnings("deprecation")
		public static void competitionRunner(DataManager data, boolean evolve){	
			CompetitionManager netManager = (CompetitionManager) data.getMethods();
			NeuralNetwork[] nns = data.getNetworks();
			int competing = Integer.parseInt("competing");
			int[] currentPlayers= new int[competing];
			for(int i = 0; i< competing; i++){
				currentPlayers[i] = i;
			}
			while(currentPlayers[0] <= Integer.parseInt(PropertyReader.getProperty("numNetworks"))-competing){
				netManager.setupCompetition(data);
				Thread[] threads = createPlayerThreads(data, evolve, netManager, nns, currentPlayers);
				for(Thread thread: threads)thread.start();
				for(Thread thread: threads)
					try {
						thread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Thread.currentThread().stop();
					}
				netManager.setEndCompetitionState(data);
				incrementPlayers(currentPlayers.length-1, data);
			}				
		}
		private static Thread[] createPlayerThreads(DataManager data, boolean evolve, CompetitionManager netManager,
				NeuralNetwork[] nns, int[] currentPlayers) {
			Thread[] threads = new Thread[currentPlayers.length];
			for(int i = 0; i<currentPlayers.length; i++){
				NeuralNetwork nn = nns[currentPlayers[i]];
				Thread thread = new Thread(){
					public void run(){
						try {
							while(!netManager.getGameOver(data)) {
								if(evolve)RunNetwork(nn);
								else {
									while(!netManager.isTurn(nn,data));
									netManager.BackIterationHandling(data);
									RunNetwork(nn);
									NeuralNetwork[] back = {nn};
									Backpropagate.backpropagate(back, data);
								}	
							}
						} catch (IllegalArgumentException |SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				threads[i] = thread;
			}
			return threads;
		}
		private static void incrementPlayers(int position, DataManager data) {
			int[] currentPlayers = data.getCurrentPlayers();
			if(currentPlayers[position] == Integer.parseInt(PropertyReader.getProperty("numNetworks"))-(currentPlayers.length-position) && position !=0){	
				incrementPlayers(position-1,data);
				currentPlayers[position] = currentPlayers[position-1]+1;
			}
			else{
				currentPlayers[position]++;
			}	
		}
}

