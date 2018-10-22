package BackEvolution.Trader;

import java.util.ArrayList;

import Backpropagate.Backpropagate;
import Evolve.Reproduce;
import FeedForward.Feedforward;
import General.DataManager;
import General.NetworkCreator;
import General.NeuralNetManager;

public class TraderDataManager extends DataManager {
	private Market[] markets;
	private TraderNetManager netManager;
	private TraderFeedforward feedforward;
	private ArrayList<Wallet> noactwallets;
	private double noact;
	private TraderBackpropagate backpropagate;
	private TraderCreator creator;

	public TraderDataManager(){
		super();
	}
	public void setMarkets(Market[] markets) {
		this.markets = markets;
		
	}
	public Market[] getMarkets() {
		return markets;
	}
	@Override
	public NeuralNetManager getNetManager() {
		if(netManager == null)netManager = new TraderNetManager(this);
		return netManager;
	}
	@Override
	public Feedforward getFeedforward() {
		if(feedforward == null)feedforward = new TraderFeedforward(this);
		return feedforward;
	}
	@Override
	public Backpropagate getBackPropagate() {
		if(backpropagate == null)backpropagate = new TraderBackpropagate(this);
		return backpropagate;
	}
	@Override
	public NetworkCreator getNetworkCreator() {
		if(creator == null)creator = new TraderCreator(this);
		return creator;
	}
	public ArrayList<Wallet> getNoActWallets() {
		// TODO Auto-generated method stub
		return noactwallets;
	}
	public void setNoActWallets(ArrayList<Wallet> noactwallets) {
		this.noactwallets = noactwallets;
		
	}
	public void setNoAct(double noact) {
		this.noact = noact;
		
	}
	public double getNoAct() {
		return noact;
	}
	
}
