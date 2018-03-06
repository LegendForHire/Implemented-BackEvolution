/**
 * NeuralNetManager.java 1.0 March 6, 2018
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
import java.util.Arrays;
import java.util.Random;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class NeuralNetManager {
	private static Singleton s;
	private static ArrayList<Wallet> noactwallets;
	public static Random rand = new Random();
	public static final double ALLOWABLE_ERROR= 250;
	public static final int TIMING = 60000;
	public static final int NUM_NETWORKS = 200;
	public static void start() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, SftpException, JSchException {
		// TODO Auto-generated method stub
		s = Singleton.getInstance();
		NeuralNetwork[] nns = NetworkCreator.CreateNetworks(NUM_NETWORKS, s.getMarkets()); 
		noactwallets = new ArrayList<Wallet>();
		URL currencies = new URL("https://bittrex.com/api/v1.1/public/getCurrencies");
		BufferedReader in = new BufferedReader(new InputStreamReader(currencies.openStream()));
		String[] currencyList = in.readLine().substring(39).split("},");
		for (String c : currencyList){
			c = c.substring(13);
			c = c.replace(c.substring(c.indexOf("\"")), "");
			noactwallets.add(new Wallet(c, 50));
		}
		
		s.setNetworks(nns);
		//This thread is the actual genetic algorithm where the neural networks evolve.
		
		Thread thread2 = new Thread() {
			
			public void run(){
				while(true) {
				Thread thread = new Thread() {
					public void run() {
					try {
						RunNetworks(nns, s.getMarkets());
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException | ClassNotFoundException | NoSuchMethodException | SecurityException | SftpException | InterruptedException e ) {
							File eFile = new File("AIError"+System.currentTimeMillis());
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
		thread2.start();
		
	}
	
	private static void RunNetworks(NeuralNetwork[] nns, Market[] markets) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, InterruptedException, SftpException {		
		int i = 1;
		
		while (true){
			for (NeuralNetwork nn : nns){
				nn.restartWallets();
			}
		double scaling = Math.log(i)*3+1;
		s.setTotalGlobalError(ALLOWABLE_ERROR/scaling + 1);
		System.out.println("Iteration " + i);
		s.getWriter().println("Iteration" + i);
		// see method description
		for(NeuralNetwork nn : nns) {
			Neuraltracker(nn);	
		}
		// this is where the back propagation learning step for the neural networks run. currently I have them set to run for one minute before evaluating
		while(s.getTotalGlobalError() > ALLOWABLE_ERROR/scaling){
			//set old values for back propagation step
			for(Market m: markets){
				m.setOld();
			}
			for (NeuralNetwork nn : nns){
				RunNetwork(nn);					
			}
			long t1 = System.currentTimeMillis();
			while(System.currentTimeMillis() - t1 < TIMING);
			Backpropagate.backpropagate(nns);		
			s.getWriter().println("Total Global Error:" + s.getTotalGlobalError()); 
		}
		s.getWriter().println("backpropagation complete");
		//Just so it's easy to keep track of how well things are doing all of the wallets are restarted to a default state at the beginning of each run
		for (NeuralNetwork nn : nns){
			nn.restartWallets();
		}
		s.getWriter().println("Wallets Restarted");
		long t1 = System.currentTimeMillis();
		while (System.currentTimeMillis()-t1 < TIMING){
			for (NeuralNetwork nn : nns){
				RunNetwork(nn);					
			}	
		}
		//Updates the fitness for each neural network 
		for (NeuralNetwork nn : nns){
			nn.updateFitness();				
		}
		s.getWriter().println("Fitness Determined");
		// determines the fitness if no action was taken
		double noact = 0;
		for (Wallet w : noactwallets){
			double amt = w.getAmmount();
			if (w.getName().equals("BTC")){
				noact+= amt;
			}
			else if (!w.getName().equals("XBB")&&!w.getName().equals("HKG")){
				for (Market market : markets){
					if (market.getMarketName().equals("BTC-" + w.getName())){
						noact += amt*market.getData(3);
					}
				
				}
			}
		}
		// sorts the neural networks from most fit to least fit.
		Arrays.sort(nns);
		//see method description
		for(NeuralNetwork nn : nns) {
			Neuraltracker(nn);	
		}
		// saves the current state of the neural networks.
		save(nns, markets, noact);
		s.setNetworks(nns);
		s.getWriter().println("Last state saved");
		//evolution method
		nns = Evolve.evolve(nns, markets);
		s.getWriter().println("Iteration " + (i++) + " Complete");
		}
		
	
	}
	//This is where a single neural network is run
		public static void RunNetwork(NeuralNetwork nn) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
			//runs each layer in order
			nn.clearInputArrays();
			for (Layer l : nn.getLayers()){			
				if (l.isInput()){
					// gets the input data, and sends it to each connected neuron.
					for (InputNeuron n : l.getINeurons()){
						n.setValue(n.input());
						for (Gene g : n.getGenes()){
							Neuron connect = g.getConnection();
							double weight = g.getWeight();	
							double value = n.getValue();
							g.setLastInput(connect.getValue()+value*weight);
							connect.setValue(connect.getValue()+value*weight);
							try{
								g.getConnection().addInput(g);
								g.setInputI(n);
							}
							catch (Exception e){
								g.getOConnection().addInput(g);
								g.setInputI(n);
							}
						}
						n.setLast(n.getValue());
						n.setValue(0.0000001);
					}
				}
				else if (l.isOutput()){
					//calls the output methods if the data that passes all the way through is enough to trigger the output neuron.
					for (OutputNeuron n : l.getONeurons()){
						if (Backpropagate.Sigmoid(n.getValue()) > Singleton.ACTIVATION){
							n.invoke();
						}
						n.setLast(Backpropagate.Sigmoid(n.getValue()));
						n.setValue(0.01);
					}
				}
				else{
					//passes the data each neuron received onto the next neurons and resets its own data state.
					for (Neuron n : l.getNeurons()){
						for (Gene g : n.getGenes()){
							Neuron connect = g.getConnection();
							double weight = g.getWeight();
							double value = Backpropagate.Sigmoid(n.getValue());
							g.setLastInput(connect.getValue()+value*weight);
							connect.setValue(connect.getValue()+value*weight);
							try{
								g.getConnection().addInput(g);
								g.setInput(n);
							}
							catch (Exception e){
								g.getOConnection().addInput(g);
								g.setInput(n);
							}
						}
						n.setLast(n.getValue());
						n.setValue(0.01);
					}
				}
			}
			
		}
		
		
	private static void save(NeuralNetwork[] nns,Market[] markets,double noact) throws IOException, SftpException {
		long t = System.currentTimeMillis();
		File out;
		File recent;
		PrintWriter fout;
		PrintWriter frecent;

			out = new File("Generation.txt");
			recent = new File("MostRecent.txt");
			fout = new PrintWriter(out);
			frecent = new PrintWriter(recent);
			frecent.println(t);
			frecent.close();
			try{
				s.getChannel().put("MostRecent.txt","MostRecent.txt");
			}
			catch(Exception e){
		
			}
		for (NeuralNetwork nn : nns){
			fout.print(nn.getLayers().size() + ";");
			for (Layer l : nn.getLayers()){
				fout.print(l.getNeurons().size() + ",");
				
			}
			fout.print(";");
			for (Layer l : nn.getLayers()){
				if (l.isInput()){
					int layernumber = l.getNumber();
					for (InputNeuron n : l.getINeurons()){
						String neurondata = n.getMarket().getMarketName() + "_" + n.getSelector();
						for (Gene g :n.getGenes()){
							Neuron nout = g.getConnection();
							if (nn.getLayers().size() == nout.getLayernumber()) {
								OutputNeuron nout2 = g.getOConnection();
								String noutnum = nout2.getMarket().getMarketName() + "_" + nout2.getSelector();
								int noutlayer = nout2.getLayernumber();
								double weight = g.getWeight();
								int enabled = g.getstate();
								fout.print(layernumber + ":" + neurondata + ":" + noutnum + ":" + noutlayer + ":" + weight + ":" + enabled + ",");
							}
							else{
								int noutnum = nout.getNumber();
								int noutlayer = nout.getLayernumber();
								double weight = g.getWeight();
								int enabled = g.getstate();
								fout.print(layernumber + ":" + neurondata + ":" + noutnum + ":" + noutlayer + ":" + weight + ":" + enabled + ",");
							}
							
						}
					}
				}
				else{
					int layernumber = l.getNumber();
					for (Neuron n : l.getNeurons()){
						int neuronnumber = n.getNumber();
						for (Gene g :n.getGenes()){
							Neuron nout = g.getConnection();
							if (nn.getLayers().size() == nout.getLayernumber()) {
								OutputNeuron nout2 = g.getOConnection();
								if(nout2 == null){
									n.RemoveGenes(g);
								}
								else{
								String noutnum = nout2.getMarket().getMarketName() + "_" + nout2.getSelector();
								int noutlayer = nout2.getLayernumber();
								double weight = g.getWeight();
								int enabled = g.getstate();
								fout.print(layernumber + ":" + neuronnumber + ":" + noutnum + ":" + noutlayer + ":" + weight + ":" + enabled + ",");
								}
							}
							else{
								int noutnum = nout.getNumber();
								int noutlayer = nout.getLayernumber();
								double weight = g.getWeight();
								int enabled = g.getstate();
								fout.print(layernumber + ":" + neuronnumber + ":" + noutnum + ":" + noutlayer + ":" + weight + ":" + enabled + ",");
							}
							
						}
					}
				}
				
			}
			int i = -1;
			while(!markets[++i].getMarketName().equals("USDT-BTC"));
			fout.println("; Created (Genarations Ago):" + nn.getAge() + "; Made (USD):" + ((nn.getFitness()-noact)*markets[i].getData(3))+ "; Global Error :" + nn.getGlobalError());
		}
		fout.close();
		try{
		s.getChannel().put("Generation.txt","Generation"+ t +".txt");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	//Makes sure neuron and gene location data are correct.
	public static void Neuraltracker(NeuralNetwork nn){
		
		for (int i = 0; i < nn.getLayers().size(); i++){
			Layer l = nn.getLayers().get(i);
			l.setNumber(i+1);
			if (l.isInput()){
				ArrayList<InputNeuron> ns = l.getINeurons();
				for (int j = 0; j < ns.size(); j++){
					InputNeuron n = ns.get(j);
					n.setLayernumber(i+1);
					n.setNumber(j+1);
				}
			}
			else if (l.isOutput()){
				ArrayList<OutputNeuron> ns = l.getONeurons();
				for (int j = 0; j < ns.size(); j++){
					OutputNeuron n = ns.get(j);
					n.setLayernumber(i+1);
					n.setNumber(j+1);
				}
			}
			else {
				ArrayList<Neuron> ns = l.getNeurons();
				for (int j = 0; j < ns.size(); j++){
					Neuron n = ns.get(j);
					n.setLayernumber(i+1);
					n.setNumber(j+1);
				}
			}
		}
		
	}


}
