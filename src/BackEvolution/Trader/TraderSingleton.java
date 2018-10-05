package BackEvolution.Trader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import General.NeuralNetwork;
import General.Singleton;

public class TraderSingleton {
	private Market[] markets;
	private static TraderSingleton uniqueInstance = new TraderSingleton();

	private TraderSingleton(){
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
	
}
