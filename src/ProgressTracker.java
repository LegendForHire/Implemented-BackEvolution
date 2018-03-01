import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;

import com.jcraft.jsch.SftpException;

public class ProgressTracker {
	public static Singleton s = Singleton.getInstance();
	private static ArrayList<Wallet> wallets;
	private static ArrayList<Wallet> noactwallets;
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
		// this thread is always running with the current most fit neural network and outputs a profits file that lets em see if the neural networks are profitable yet.
		Thread thread3 = new Thread(){
			public void run(){
				while(true) {
					Thread thread = new Thread() {
						public void run() {
				try {
					main(s.getMarkets());
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException | IOException | SftpException e) {
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
		while(System.currentTimeMillis()-t1 < 86400000);
		thread3.start();
	}
	public static void main(Market[] markets) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SftpException {
		while(true){
		Singleton s = Singleton.getInstance();
		NeuralNetwork nn = s.getNetworks()[0];
		NeuralNetManager.RunNetwork(nn);
		long t = System.currentTimeMillis();
		while(System.currentTimeMillis()-t > 60000);
		File f2 = new File("profit.txt");
		PrintWriter fout = new PrintWriter(f2);
		double noact = 0;
		
		int i = -1;		
		while(!markets[++i].getMarketName().equals("USDT-BTC"));
		for(Wallet w : noactwallets) {
			int j = -1;
			while(!markets[++j].getMarketName().equals(w.getName()));
			noact += w.getAmmount()*markets[j].getData(3);
		}
		fout.println("Total Profit= " + (nn.getFitness()-noact)*markets[i].getData(3));
		fout.close();
		try{
			s.getChannel().put("profit.txt","profit.txt");
		 }
		catch(Exception e){
				
		}
			
		}		
	}
	
}
