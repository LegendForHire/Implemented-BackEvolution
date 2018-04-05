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
	public static final double  WEIGHT_ADJUST = .65;
	public static final double  RANDOM_WEIGHT = .1+WEIGHT_ADJUST;
	public static final double ENABLE_DISABLE = .05+RANDOM_WEIGHT;
	public static final double  NEW_GENE = ENABLE_DISABLE + .17 ;
	public static final double EXISTING_LAYER = NEW_GENE + .029;

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
	@Override
	public double getDisableProbability() {
		// TODO Auto-generated method stub
		return ENABLE_DISABLE;
	}
	@Override
	public double getAdjustProbability() {
		// TODO Auto-generated method stub
		return WEIGHT_ADJUST;
	}
	@Override
	public double getRandomProbability() {
		// TODO Auto-generated method stub
		return RANDOM_WEIGHT;
	}
	@Override
	public double getNewGeneProbability() {
		// TODO Auto-generated method stub
		return NEW_GENE;
	}
	@Override
	public double getExistingLayerProbability() {
		// TODO Auto-generated method stub
		return EXISTING_LAYER;
	}
}
