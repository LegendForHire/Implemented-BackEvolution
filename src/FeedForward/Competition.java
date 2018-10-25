package FeedForward;

import General.DataManager;
import General.PropertyReader;
import NeuralNetwork.NeuralNetwork;

public abstract class Competition extends Feedforward {

	public Competition(DataManager data) {
		super(data);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void feed(boolean evolve){	
		NeuralNetwork[] nns = data.getNetworks();
		int competing = Integer.parseInt("competing");
		int[] currentPlayers= new int[competing];
		for(int i = 0; i< competing; i++){
			currentPlayers[i] = i;
		}
		while(currentPlayers[0] <= Integer.parseInt(PropertyReader.getProperty("numNetworks"))-competing){
			setupCompetition();
			Thread[] threads = createPlayerThreads(evolve, nns, currentPlayers);
			for(Thread thread: threads)thread.start();
			for(Thread thread: threads)
				try {
					thread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Thread.currentThread().stop();
				}
			setEndCompetitionState();
			incrementPlayers(currentPlayers.length-1);
		}				
	}
	private Thread[] createPlayerThreads(boolean evolve,
			NeuralNetwork[] nns, int[] currentPlayers) {
		Thread[] threads = new Thread[currentPlayers.length];
		for(int i = 0; i<currentPlayers.length; i++){
			NeuralNetwork nn = nns[currentPlayers[i]];
			Thread thread = new Thread(){
				public void run(){
					try {
						while(getGameOver()) {
							if(evolve)RunNetwork(nn);
							else {
								while(isTurn(nn));
								BackIterationHandling();
								RunNetwork(nn);
								NeuralNetwork[] back = {nn};
								backpropagate.backpropagate(back);
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
	private void incrementPlayers(int position) {
		int[] currentPlayers = data.getCurrentPlayers();
		if(currentPlayers[position] == Integer.parseInt(PropertyReader.getProperty("numNetworks"))-(currentPlayers.length-position) && position !=0){	
			incrementPlayers(position-1);
			currentPlayers[position] = currentPlayers[position-1]+1;
		}
		else{
			currentPlayers[position]++;
		}	
	}
	protected abstract void setupCompetition();
	protected abstract boolean getGameOver();
	protected abstract boolean isTurn(NeuralNetwork nn);
	protected abstract void setEndCompetitionState();
}
