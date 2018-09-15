package BackEvolution.Brawlhalla;

import java.io.PrintWriter;
import java.util.ArrayList;

import Backpropagate.BackpropagateSingleton;
import Competitive.CompetitionSingleton;
import General.NeuralNetwork;

public class BrawlhallaSingleton implements BackpropagateSingleton, CompetitionSingleton{
	private static BrawlhallaSingleton uniqueInstance = new BrawlhallaSingleton();
	private double totalGlobalError;
	private PrintWriter writer;
	private NeuralNetwork[] nns;
	private int Gen;
	private Controller[] controllers;
	private Game game;
	private double[][] state;
	private String lastAction;
	private int[] currentPlayers;
	private ArrayList<Legend> legends;
	private ArrayList<Weapon> weapons;
	private ArrayList<Stage> stages;
	private long id=0;

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
	public void incrementGen() {
		Gen++;
		
	}
	@Override
	public int getGen() {
		return Gen;
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
	public void setLegends() {
		legends = game.getLegends();
		
	}
	public void setWeapons() {
		weapons = game.getWeapons();
		
	}
	public void setStages() {
		stages = game.getStages();
		
	}
	@Override
	public long getNewID() {
		// TODO Auto-generated method stub
		id++;
		return id;
	}
	public void setLastID(long num) {
		if(num > id)id = num;
	}
}
