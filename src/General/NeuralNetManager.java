package General;
/**
 * NeuralNetManager.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import Evolve.Evolve;
import FeedForward.Feedforward;
import NeuralNetwork.Gene;
import NeuralNetwork.Layer;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.Neuron;
public abstract class NeuralNetManager {
	protected DataManager data;
	public NeuralNetManager(DataManager data) {
		this.data = data;
	}
	public void start() {
		//Creating the networks to run them
		setup();
		System.out.println("Creating Networks");
		NetworkCreator creator = data.getNetworkCreator();
		NeuralNetwork[] nns =  creator.CreateNetworks(); 
		data.setNetworks(nns);
		System.out.println("Networks Created\nStartingLearningThreads");
		//Thread to keep the networks learning
		Thread thread2 = new Thread() {			
			public void run(){
				while(true) {
				Thread thread = new Thread() {
				public void run() {
					try {
						RunNetworks();
					} catch (IllegalArgumentException | SecurityException e) {
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
	
	//runs the networks
	protected void RunNetworks(){
			Feedforward feed = data.getFeedforward();
			Evolve evolve = data.getEvolve();
			
			while(true) {
				System.out.println("New Generation " + data.getGen() + " Started\nGathering data and backpropagting");
				feed.backpropagateRunner();
				System.out.println("Backpropagation complete \nGathering data for evolution");
				feed.evolveRunner();
				// saves the current state of the neural networks.
				System.out.println("Data Collection Complete\nSaving Current State");
				NeuralNetwork[] nns = data.getNetworks();
				save();
				data.getWriter().println("Current State Saved\nEvolving");
				//evolution method
				nns = evolve.evolve(nns);
				System.out.println("Evolution Complete\nNew Generation created");
				data.setNetworks(nns);
				data.getWriter().println("Iteration " + (data.getGen()) + " Complete");
				data.incrementGen();
			}
	}				
	//save method
	private void save(){
		NeuralNetwork[] nns = data.getNetworks();
		long t = System.currentTimeMillis();
		File out;
		File recent;
		PrintWriter fout;
		PrintWriter frecent;

			out = new File("Generation.txt");
			recent = new File("MostRecent.txt");
			try {
				fout = new PrintWriter(out);
				frecent = new PrintWriter(recent);
				frecent.println(t);
				frecent.close();
	
		
		for (NeuralNetwork nn : nns){
			fout.print(nn.getLayers().size() + ";");
			for (Layer l : nn.getLayers()){
				fout.print(l.getNeurons().size() + ",");
			}
			fout.print(";");
			for (Layer l : nn.getLayers()){
				int layernumber = l.getNumber();
				for (Neuron n : l.getNeurons()){
					String neurondata;
					if (l.isInput())neurondata = saveInput(n);
					else neurondata = "" + n.getNumber();
					for (Gene g :n.getGenes()){
						geneSave(fout, nn, layernumber, n, neurondata, g, data);
					}
				}
			}
			fout.println();
		}
		fout.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void geneSave(PrintWriter fout, NeuralNetwork nn, int layernumber,
			Neuron n, String neurondata, Gene g, DataManager data) {
		Neuron nout = g.getConnection();
		String noutnum = null;
		if (nn.getLayers().size() == nout.getLayernumber()) {
			noutnum = saveOutput(nout);
		}
		else noutnum = "" + nout.getNumber();
		if(noutnum != null) {
			int noutlayer = nout.getLayernumber();
			double weight = g.getWeight();
			long id = g.getID();
			fout.print(layernumber + ":" + neurondata + ":" + noutnum + ":" + noutlayer + ":" + weight + ":" + id + ",");;						
		}
	}
	//override this method if your input neuron positions could change between loads
	// give each neuron a unique name based on its unique data
	//make sure your returns do not include colons or semicolons or new lines
	public String saveInput(Neuron in) {
		return "" + in.getNumber();
	}
	//override this method if your output neuron positions could change between loads
	// give each neuron a unique name based on its unique data
	//make sure your returns do not include colons or semicolons or new lines
	public String saveOutput(Neuron out) {
		return "" + out.getNumber();
	}
	//override this method if you have additional information you want
	// do not include new lines here
	public String saveMetaData(NeuralNetwork nn) {
		return "";
	}
	//This runs before RunNetworks is called and setups up the single
	//use items for further calls to this class item.
	public abstract void setup();



}
