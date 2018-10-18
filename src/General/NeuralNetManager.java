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
import java.util.ArrayList;

import Backpropagate.Backpropagate;
import Evolve.Evolve;
public abstract class NeuralNetManager {
	public static void start(DataManager data) {
		//Creating the networks to run them
		MethodManager manager = data.getMethods();
		manager.setup(data);
		NeuralNetwork[] nns =  NetworkCreator.CreateNetworks(data); 
		data.setNetworks(nns);
		//Thread to keep the networks learning
		Thread thread2 = new Thread() {			
			public void run(){
				while(true) {
				Thread thread = new Thread() {
				public void run() {
					try {
						RunNetworks(data);
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
	protected static void RunNetworks(DataManager data){
			while(true) {
				Backpropagate.runner(data);
				Evolve.runner(data);
				// saves the current state of the neural networks.
				NeuralNetwork[] nns = data.getNetworks();
				save(data);
				data.getWriter().println("Last state saved");
				//evolution method
				nns = Evolve.evolve(nns, data);
				data.setNetworks(nns);
				data.getWriter().println("Iteration " + (data.getGen()) + " Complete");
				data.incrementGen();
			}
	}
	//This is where a single neural network is run
	public static void RunNetwork(NeuralNetwork nn){
			//runs each layer in order
			nn.clearInputArrays();
			for (Layer l : nn.getLayers()){			
				// gets the input data, and sends it to each connected neuron.
				for (Neuron n : l.getNeurons()){
					if(l.isInput() || (l.isOutput() && Backpropagate.Sigmoid(n.getValue()) > Double.parseDouble(PropertyReader.getProperty("activation"))))n.invoke();
					if(!l.isOutput())runGenes(n);
					n.setLast(n.getValue());
					n.setValue(0.0000001);
				}
			}
			
		}
	private static void runGenes(Neuron n) {
		for (Gene g : n.getGenes()){
			Neuron connect = g.getConnection();
			double weight = g.getWeight();
			double value = Backpropagate.Sigmoid(n.getValue());
			g.setLastInput(connect.getValue()+value*weight);
			connect.setValue(connect.getValue()+value*weight);
			g.getConnection().addInput(g);
			g.setInput(n);
		}
	}				
	//save method
	private static void save(DataManager data){
		NeuralNetwork[] nns = data.getNetworks();
		MethodManager manager = data.getMethods();
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
					if (l.isInput())neurondata = manager.saveInput(n, data);
					else neurondata = "" + n.getNumber();
					for (Gene g :n.getGenes()){
						geneSave(manager, fout, nn, layernumber, n, neurondata, g, data);
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
	@SuppressWarnings("unused")
	private static void geneSave(MethodManager manager, PrintWriter fout, NeuralNetwork nn, int layernumber,
			Neuron n, String neurondata, Gene g, DataManager data) {
		Neuron nout = g.getConnection();
		String noutnum = null;
		if (nn.getLayers().size() == nout.getLayernumber()) {
			if(nout!=null)noutnum = manager.saveOutput(nout, data);
			else n.RemoveGenes(g);
		}
		else noutnum = "" + nout.getNumber();
		if(noutnum != null) {
			int noutlayer = nout.getLayernumber();
			double weight = g.getWeight();
			long id = g.getID();
			fout.print(layernumber + ":" + neurondata + ":" + noutnum + ":" + noutlayer + ":" + weight + ":" + id + ",");;						
		}
	}
	//Makes sure neuron and gene location data are correct.
	public static void Neuraltracker(NeuralNetwork nn){		
		for (int i = 0; i < nn.getLayers().size(); i++){
			Layer l = nn.getLayers().get(i);
			l.setNumber(i+1);		
			ArrayList<Neuron> ns = l.getNeurons();
			for (int j = 0; j < ns.size(); j++){
				Neuron n = ns.get(j);
				n.setLayernumber(i+1);
				n.setNumber(j+1);
			}
		}
		
	}


}
