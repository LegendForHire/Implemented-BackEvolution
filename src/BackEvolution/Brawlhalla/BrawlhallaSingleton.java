package BackEvolution.Brawlhalla;

import java.io.PrintWriter;

import General.NeuralNetwork;
import General.Singleton;

public class BrawlhallaSingleton implements Singleton{
	private static final int TIMING = 0;
	private static final double ACTIVATION = .8;
	private static final String TYPE = "Brawlhalla";
	private static final int NUM_NETWORKS = 200;
	private static final double LEARNING_RATE = .01;
	private static final double MOMENTUM = .25;
	private static final double ALLOWED_ERROR = 250;
	private static final int NUM_COMPETING = 2;
	private static BrawlhallaSingleton uniqueInstance = new BrawlhallaSingleton();
	private double totalGlobalError;
	private PrintWriter writer;
	private NeuralNetwork[] nns;
	private int Gen;
	private static final int LEARNTYPE = 1;
	private Controller[] controllers;
	private Game game;
	private double[][] state;

	private BrawlhallaSingleton(){
		
	}
	public static BrawlhallaSingleton getInstance(){
		return uniqueInstance; 
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
	public PrintWriter getWriter() {
		return writer;
	}

	@Override
	public void setWriter(PrintWriter w) {
		writer = w;
		
	}

	@Override
	public void setNetworks(NeuralNetwork[] nns) {
		this.nns = nns;
		
	}

	@Override
	public NeuralNetwork[] getNetworks() {
		return nns;
	}

	@Override
	public int getTiming() {
		return TIMING;
	}

	@Override
	public double getActivation() {
		return ACTIVATION;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public void incrementGen() {
		Gen++;
		
	}

	@Override
	public int getGen() {
		return Gen;
	}

	@Override
	public int getNumNetworks() {
		return NUM_NETWORKS;
	}

	@Override
	public double getLearningRate() {
		return LEARNING_RATE;
	}

	@Override
	public double getMomentum() {
		return MOMENTUM;
	}

	@Override
	public double getAllowedError() {
		return ALLOWED_ERROR;
	}

	@Override
	public int numCompeting() {
		return NUM_COMPETING;
	}
	@Override
	public int getLearningType() {
		// TODO Auto-generated method stub
		return LEARNTYPE;
	}
	public Controller[] getControllers() {
		return controllers;
	}
	public void setControllers(Controller[] controllers) {
		this.controllers = controllers;
	}
	public void setGame(Game game) {
		this.game = game;		
	}
	public Game getGame(){
		return game;
	}
	public void setLastState() {
		state = game.getState();
	}
	public double[][] getLastState() {
		return state;
	}
	public Weapon[] getWeapons() {
		// TODO Auto-generated method stub
		return null;
	}
	public Legend[] getLegends() {
		// TODO Auto-generated method stub
		return null;
	}
	public Stage[] getStages() {
		// TODO Auto-generated method stub
		return null;
	}

}
