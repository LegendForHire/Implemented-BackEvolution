package Competitive;

import Backpropagate.Backpropagate;
import General.DataManager;
import General.NeuralNetManager;
import General.NeuralNetwork;
import General.PropertyReader;

public class Competition {
	@SuppressWarnings("deprecation")
	public static void backpropagationRunner(DataManager data){	
		CompetitionManager netManager = netManagerReflected();
		NeuralNetwork[] nns = data.getNetworks();
		int competing = Integer.parseInt("competing");
		int[] currentPlayers= new int[competing];
		for(int i = 0; i< competing; i++){
			currentPlayers[i] = i;
		}
		while(currentPlayers[0] <= Integer.parseInt(PropertyReader.getProperty("numNetworks"))-competing){
			netManager.setupCompetition(data);
			Thread[] threads = new Thread[currentPlayers.length];
			for(int i = 0; i<currentPlayers.length; i++){
				NeuralNetwork nn = nns[currentPlayers[i]];
				Thread thread = new Thread(){
					public void run(){
						try {
							while(!netManager.getGameOver(data)) {
								while(!netManager.isTurn(nn,data))
								Backpropagate.BackIterationHandling(data);
								NeuralNetManager.RunNetwork(nn);
								NeuralNetwork[] back = {nn};
								Backpropagate.backpropagate(back, data);
							}
						} catch (IllegalArgumentException |SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				threads[i] = thread;
			}
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
	@SuppressWarnings("deprecation")
	public static void evolutionRunner(DataManager data){		
		CompetitionManager netManager = netManagerReflected();
		NeuralNetwork[] nns = data.getNetworks();
		int competing = Integer.parseInt("competing");
		int[] currentPlayers= new int[competing];
		for(int i = 0; i< competing; i++){
			currentPlayers[i] = i;
		}
		while(currentPlayers[0] <= Integer.parseInt(PropertyReader.getProperty("numNetworks"))-competing){
			netManager.setupCompetition(data);
			Thread[] threads = new Thread[currentPlayers.length];
			for(int i = 0; i<currentPlayers.length; i++){
				NeuralNetwork nn = nns[currentPlayers[i]];
				Thread thread = new Thread(){
					public void run(){
						NeuralNetManager.RunNetwork(nn);
					}
				};
				threads[i] = thread;
			}
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
			incrementPlayers(data.getCurrentPlayers().length-1, data);
		}				
	}
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static CompetitionManager netManagerReflected() {		
		String type = PropertyReader.getProperty("type");
		try {
			Class<? extends CompetitionManager> class1 = (Class<? extends CompetitionManager>) Class.forName("BackEvolution."+type+"."+type+"NetManager");
			CompetitionManager netManager = class1.newInstance();
			return netManager;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Thread.currentThread().stop();
			
		}
		return null;
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
