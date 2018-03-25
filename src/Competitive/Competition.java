package Competitive;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import General.NeuralNetManager;
import General.NeuralNetwork;
import General.Singleton;

public class Competition {
	@SuppressWarnings({"unchecked","deprecation"})
	public static void runner(Singleton s) throws InstantiationException, IllegalAccessException, ClassNotFoundException{		
		Class<? extends CompetitionManager> class1 = (Class<? extends CompetitionManager>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"NetManager");
		CompetitionManager netManager = class1.newInstance();
		NeuralNetwork[] nns = s.getNetworks();
		int[] currentPlayers= new int[s.numCompeting()];
		for(int i = 0; i< s.numCompeting(); i++){
			currentPlayers[i] = i;
		}
		while(currentPlayers[0] <= s.getNumNetworks()-s.numCompeting()){
			netManager.setupCompetition(currentPlayers);
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
			incrementPlayers(currentPlayers,currentPlayers.length-1, s);
		}				
	}
	public static void incrementPlayers(int[] currentPlayers, int position, Singleton s) {
		if(currentPlayers[position] == s.getNumNetworks()-(currentPlayers.length-position) && position !=0){	
			incrementPlayers(currentPlayers,position-1,s);
			currentPlayers[position] = currentPlayers[position-1]+1;
		}
		else{
			currentPlayers[position]++;
		}	
	}
}
