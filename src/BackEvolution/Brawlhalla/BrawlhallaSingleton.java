package BackEvolution.Brawlhalla;

import java.io.PrintWriter;
import java.util.ArrayList;

import Backpropagate.BackpropagateSingleton;
import Competitive.CompetitionSingleton;
import Evolve.EvolveSingleton;
import General.NeuralNetwork;

public class BrawlhallaSingleton implements EvolveSingleton,BackpropagateSingleton, CompetitionSingleton{
	private static final int TIMING = 0;
	private static final double ACTIVATION = .8;
	private static final String TYPE = "Brawlhalla";
	private static final int NUM_NETWORKS = 200;
	private static final double LEARNING_RATE = .01;
	private static final double MOMENTUM = .25;
	private static final double ALLOWED_ERROR = 250;
	private static final int NUM_COMPETING = 2;
	public static final double  WEIGHT_ADJUST = .65;
	public static final double  RANDOM_WEIGHT = .1+WEIGHT_ADJUST;
	public static final double ENABLE_DISABLE = .05+RANDOM_WEIGHT;
	public static final double  NEW_GENE = ENABLE_DISABLE + .17 ;
	public static final double EXISTING_LAYER = NEW_GENE + .029;
	private static BrawlhallaSingleton uniqueInstance = new BrawlhallaSingleton();
	private double totalGlobalError;
	private PrintWriter writer;
	private NeuralNetwork[] nns;
	private int Gen;
	private static final int LEARNTYPE = 1;
	private Controller[] controllers;
	private Game game;
	private double[][] state;
	private String lastAction;
	private int[] currentPlayers;
	private ArrayList<Legend> legends;
	private ArrayList<Weapon> weapons;
	private ArrayList<Stage> stages;

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
	public ArrayList<Weapon> getWeapons() {
		return weapons;
	}
	public ArrayList<Legend> getLegends() {
		return legends;
	}
	public ArrayList<Stage> getStages() {
		return stages;
	}
	public String getLastAction() {
		return lastAction;
	}
	public int[] getCurrentPlayers() {
		return currentPlayers;
	}
	@Override
	public void setCurrentPlayers(int[] players) {
		currentPlayers = players;
		
	}
	@Override
	public double getDisableProbability() {
		return ENABLE_DISABLE;
	}
	@Override
	public double getAdjustProbability() {
		return WEIGHT_ADJUST;
	}
	@Override
	public double getRandomProbability() {
		return RANDOM_WEIGHT;
	}
	@Override
	public double getNewGeneProbability() {
		return NEW_GENE;
	}
	@Override
	public double getExistingLayerProbability() {
		return EXISTING_LAYER;
	}
	public void setLegends() {
		legends = game.getLegends();
		
	}
	public void setWeapons() {
		weapons = game.getWeapons();
		
	}
	public void setStages() {
		stages = game.getStages();
		
	}

}
