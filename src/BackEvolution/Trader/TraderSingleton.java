package BackEvolution.Trader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import Backpropagate.BackpropagateSingleton;
import Evolve.EvolveSingleton;
import General.NeuralNetwork;

public class TraderSingleton implements EvolveSingleton, BackpropagateSingleton {
	private Market[] markets;
	private NeuralNetwork[] networks;
	private double totalGlobalError;
	public static final double ACTIVATION = .8;
	public static final int NUM_NETWORKS = 200;
	private static TraderSingleton uniqueInstance = new TraderSingleton();
	private PrintWriter writer;
	private int Generation;
	public static final double ALLOWABLE_ERROR= 250;
	public static final int TIMING = 60000;
	public static final double LEARNING_RATE = .01;
	public static final double MOMENTUM = .25;
	private static final String TYPE = "Trader";
	private static final int NUM_COMPETING = 1;
	private static final int LEARNTYPE = 1;
	private TraderSingleton(){
		File f = new File("log.txt");
		try {
			writer = new PrintWriter(f);
		} catch (FileNotFoundException e) {}
		Generation = 0;
	}
	@Override
	public PrintWriter getWriter() {
		return writer;
	}
	@Override
	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}
	public static TraderSingleton getInstance() {
		return uniqueInstance;
	}
	public void setMarkets(Market[] markets) {
		this.markets = markets;
		
	}

	public Market[] getMarkets() {
		return markets;
	}
	@Override
	public double getTotalGlobalError() {
		return totalGlobalError;
	}
	@Override
	public void setTotalGlobalError(double totalGlobalError) {
		this.totalGlobalError = totalGlobalError;
	}
	@Override
	public double getAllowedError(){
		return ALLOWABLE_ERROR;	
	}
	@Override
	public int getTiming(){
		return TIMING;
	}
	@Override
	public double getActivation() {
		// TODO Auto-generated method stub
		return ACTIVATION;
	}
	@Override
	public String getType() {
		return TYPE;
	}
	@Override
	public void incrementGen() {
		Generation = Generation+1;		
	}
	@Override
	public int getGen() {
		// TODO Auto-generated method stub
		return Generation;
	}
	@Override
	public int getNumNetworks() {
		return NUM_NETWORKS;
		
	}
	public double getLearningRate() {
		return LEARNING_RATE;
	}
	public double getMomentum() {
		return MOMENTUM;
	}
	public int numCompeting() {
		return NUM_COMPETING;
	}
	public int getLearningType() {
		return LEARNTYPE;
	}
	@Override
	public NeuralNetwork[] getNetworks() {
		// TODO Auto-generated method stub
		return networks;
	}
	@Override
	public void setNetworks(NeuralNetwork[] networks) {
		// TODO Auto-generated method stub
		this.networks = networks;
	}
}
