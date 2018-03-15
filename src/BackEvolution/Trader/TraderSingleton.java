package BackEvolution.Trader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import BackEvolution.NeuralNetwork;
import BackEvolution.Singleton;

public class TraderSingleton implements Singleton {
	private Market[] markets;
	private NeuralNetwork[] networks;
	private double totalGlobalError;
	public static final double ACTIVATION = .8;
	public static final int NUM_NETWORKS = 200;
	private static TraderSingleton uniqueInstance = new TraderSingleton();
	private PrintWriter writer;
	public double allowed;
	private int timing;
	private double activation;
	private int Generation;
	private int numNetworks;
	private double learningRate;
	private double momentum;
	private static final String TYPE = "Trader";
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
		// TODO Auto-generated method stub
		return markets;
	}
	@Override
	public void setNetworks(NeuralNetwork[] nns) {
		// TODO Auto-generated method stub
		networks = nns;
	}
	@Override
	public NeuralNetwork[] getNetworks() {
		// TODO Auto-generated method stub
		return networks;
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
	public double getAllowed(){
		return allowed;	
	}
	@Override
	public void setAllowed(double a){
		allowed = a;
	}
	@Override
	public int getTiming(){
		return timing;
	}
	@Override
	public void setTiming(int i){
		timing = i;
	}
	@Override
	public double getActivation() {
		// TODO Auto-generated method stub
		return activation;
	}
	@Override
	public void setActivation(double d) {
		activation = d;
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
	public void setNumNetworks(int numNetworks) {
		this.numNetworks = numNetworks;
		
	}
	@Override
	public int getNumNetworks() {
		return numNetworks;
		
	}
	@Override
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
		
	}
	public double getLearningRate() {
		return learningRate;
	}
	public double getMomentum() {
		return momentum;
	}
	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}
}
