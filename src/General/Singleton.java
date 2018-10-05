package General;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Singleton.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */

public class Singleton {
	private static Singleton uniqueInstance = new Singleton();
	private NeuralNetwork[] networks;
	private double totalGlobalError;
	public static final int NUM_NETWORKS = 200;
	private PrintWriter writer;
	private int Generation = 0;
	private long id = 0;
	private int[] currentPlayers;
	//create fields for all items that have getters and setter
	//create constants for all methods with only getters. 
	//these are your twiddly knobs for changing how the algorithm runs a bit.
	// also when implementing create the getInstance method of the singleton
	// and at least an empty constructor
	private Singleton(){
		File f = new File("log.txt");
		try {
			writer = new PrintWriter(f);
		} catch (FileNotFoundException e) {}
		Generation = 0;
	}
	public static Singleton getInstance() {
		return uniqueInstance;
	}
	public PrintWriter getWriter() {
		return writer;
	}
	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}
	public double getTotalGlobalError() {
		return totalGlobalError;
	}
	public void setTotalGlobalError(double totalGlobalError) {
		this.totalGlobalError = totalGlobalError;
	}
	public void incrementGen() {
		Generation = Generation+1;		
	}
	public int getGen() {
		// TODO Auto-generated method stub
		return Generation;
	}
	public NeuralNetwork[] getNetworks() {
		// TODO Auto-generated method stub
		return networks;
	}
	public void setNetworks(NeuralNetwork[] networks) {
		// TODO Auto-generated method stub
		this.networks = networks;
	}
	public long getNewID() {
		// TODO Auto-generated method stub
		id++;
		return id;
	}
	public void setLastID(long num) {
		if(num > id)id = num;
	}
	public int[] getCurrentPlayers() {
		return currentPlayers;
	};
	public void setCurrentPlayers(int[] players) {
		currentPlayers = players;
	}
}

