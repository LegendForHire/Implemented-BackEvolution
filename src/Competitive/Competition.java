package Competitive;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import Backpropagate.Backpropagate;
import General.NeuralNetManager;
import General.NeuralNetwork;
import General.PropertyReader;
import General.Singleton;

public class Competition {
	public static void backpropagationRunner(Singleton s) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException{	
		CompetitionManager netManager = netManagerReflected(s);
		NeuralNetwork[] nns = s.getNetworks();
		int competing = Integer.parseInt("competing");
		int[] currentPlayers= new int[competing];
		for(int i = 0; i< competing; i++){
			currentPlayers[i] = i;
		}
		while(currentPlayers[0] <= Integer.parseInt(PropertyReader.getProperty("numNetworks"))-competing){
			netManager.setupCompetition();
			Thread[] threads = new Thread[currentPlayers.length];
			for(int i = 0; i<currentPlayers.length; i++){
				NeuralNetwork nn = nns[currentPlayers[i]];
				Thread thread = new Thread(){
					public void run(){
						try {
							while(!netManager.getGameOver()) {
								while(!netManager.isTurn(nn))
								Backpropagate.BackIterationHandling(s);
								NeuralNetManager.RunNetwork(nn,s);
								NeuralNetwork[] back = {nn};
								Backpropagate.backpropagate(back, s);
							}
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| IOException | ClassNotFoundException | SecurityException | InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				threads[i] = thread;
			}
			for(Thread thread: threads)thread.start();
			for(Thread thread: threads)thread.join();
			netManager.setEndCompetitionState();
			incrementPlayers(currentPlayers.length-1, s);
		}				
	}
	public static void evolutionRunner(Singleton s) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException{		
		CompetitionManager netManager = netManagerReflected(s);
		NeuralNetwork[] nns = s.getNetworks();
		int competing = Integer.parseInt("competing");
		int[] currentPlayers= new int[competing];
		for(int i = 0; i< competing; i++){
			currentPlayers[i] = i;
		}
		while(currentPlayers[0] <= Integer.parseInt(PropertyReader.getProperty("numNetworks"))-competing){
			netManager.setupCompetition();
			Thread[] threads = new Thread[currentPlayers.length];
			for(int i = 0; i<currentPlayers.length; i++){
				NeuralNetwork nn = nns[currentPlayers[i]];
				Thread thread = new Thread(){
					public void run(){
						try {
							NeuralNetManager.RunNetwork(nn,s);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				threads[i] = thread;
			}
			for(Thread thread: threads)thread.start();
			for(Thread thread: threads)thread.join(); 
			netManager.setEndCompetitionState();
			incrementPlayers(s.getCurrentPlayers().length-1, s);
		}				
	}
	private static CompetitionManager netManagerReflected(Singleton s)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {		
		String type = PropertyReader.getProperty("type");
		@SuppressWarnings("unchecked")
		Class<? extends CompetitionManager> class1 = (Class<? extends CompetitionManager>) Class.forName("BackEvolution."+type+"."+type+"NetManager");
		@SuppressWarnings("deprecation")
		CompetitionManager netManager = class1.newInstance();
		return netManager;
	}
	private static void incrementPlayers(int position, Singleton s) {
		int[] currentPlayers = s.getCurrentPlayers();
		if(currentPlayers[position] == Integer.parseInt(PropertyReader.getProperty("numNetworks"))-(currentPlayers.length-position) && position !=0){	
			incrementPlayers(position-1,s);
			currentPlayers[position] = currentPlayers[position-1]+1;
		}
		else{
			currentPlayers[position]++;
		}	
	}
}
