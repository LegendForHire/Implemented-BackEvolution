package BackEvolution.Trader;
/**
 * ProgressTracker.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;

import General.NeuralNetManager;
import General.NeuralNetwork;
public class ProgressTracker {
	public static TraderSingleton s = TraderSingleton.getInstance();
	private static ArrayList<Wallet> wallets;
	private static ArrayList<Wallet> noactwallets;
	private static Market BTC;
	public static final int UPDATE_TIMING = 60000;
	public static final int WAIT_TIME = 86400000;
	public static void start() throws IOException {
		wallets = new ArrayList<Wallet>();
		noactwallets = new ArrayList<Wallet>();
		URL currencies = new URL("https://bittrex.com/api/v1.1/public/getCurrencies");
		BufferedReader in = new BufferedReader(new InputStreamReader(currencies.openStream()));
		String[] currencyList = in.readLine().substring(39).split("},");
		for (String c : currencyList){
			c = c.substring(13);
			c = c.replace(c.substring(c.indexOf("\"")), "");
			wallets.add(new Wallet(c, 50));
			noactwallets.add(new Wallet(c, 50));
		}
		int i = -1;	
		Market[] markets = s.getMarkets();
		while(!markets[++i].getMarketName().equals("USDT-BTC"));
		BTC = markets[i];
		// this thread is always running with the current most fit neural network and outputs a profits file that lets em see if the neural networks are profitable yet.
		Thread thread3 = new Thread(){
			public void run(){
				while(true) {
					Thread thread = new Thread() {
						public void run() {
				try {
					main(s.getMarkets());
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException | IOException e) {
					File eFile = new File("ProfitError"+System.currentTimeMillis());
					try {
						PrintWriter eWriter = new PrintWriter(eFile);
						e.printStackTrace(eWriter);
						eWriter.close();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
						}
					};
					thread.start();
					try {
						thread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		long t1 = System.currentTimeMillis();
		while(System.currentTimeMillis()-t1 < WAIT_TIME);
		thread3.start();
	}
	public static void main(Market[] markets) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		while(true){
		NeuralNetwork nn = s.getNetworks()[0];
		NeuralNetManager.RunNetwork(nn,s);
		long t = System.currentTimeMillis();
		while(System.currentTimeMillis()-t > UPDATE_TIMING);
		File f2 = new File("profit.txt");
		PrintWriter fout = new PrintWriter(f2);
		double noact = 0;		
		for (Wallet w : noactwallets){
			double amt = w.getAmmount();
			if (w.getName().equals("BTC")){
				noact+= amt;
			}
			else if(!w.getName().equals("XBB")&&!w.getName().equals("HKG")){
				for (Market market : markets){
					if (market.getMarketName().equals("BTC-" + w.getName())){
						noact += amt*market.getData(3);
					}
				
				}
			}
		}
		fout.println("Total Profit= " + (nn.getFitness()-noact)*BTC.getData(3));
		fout.close();
			
		}		
	}
	
}
