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
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import Backpropagate.Backpropagate;
import Evolve.Evolve;
public abstract class NeuralNetManager {
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void start(Singleton s) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, InstantiationException {
		//Creating the networks to run them
		String type = PropertyReader.getProperty("type");
		Class<? extends SpecialNetManager> class1 = (Class<? extends SpecialNetManager>) Class.forName("BackEvolution."+type+"."+type+"NetManager");
		SpecialNetManager netManager = class1.newInstance();
		netManager.setup();
		NeuralNetwork[] nns =  NetworkCreator.CreateNetworks(s); 
		s.setNetworks(nns);
		//Thread to keep the networks learning
		Thread thread2 = new Thread() {			
			public void run(){
				while(true) {
				Thread thread = new Thread() {
					public void run() {
					try {
						RunNetworks(s);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IOException | InterruptedException e) {
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
	protected static void RunNetworks(Singleton s) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, InterruptedException, InstantiationException {
			while(true) {
				Backpropagate.runner(s);
				Evolve.runner(s);
				// saves the current state of the neural networks.
				NeuralNetwork[] nns = s.getNetworks();
				save(s);
				s.getWriter().println("Last state saved");
				//evolution method
				nns = Evolve.evolve(nns, s);
				s.setNetworks(nns);
				s.getWriter().println("Iteration " + (s.getGen()) + " Complete");
				s.incrementGen();
			}
	}
	//This is where a single neural network is run
	public static void RunNetwork(NeuralNetwork nn,Singleton s) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
			//runs each layer in order
			nn.clearInputArrays();
			for (Layer l : nn.getLayers()){			
				if (l.isInput()){
					// gets the input data, and sends it to each connected neuron.
					for (Neuron n : l.getNeurons()){
						n.invoke();
						for (Gene g : n.getGenes()){
							Neuron connect = g.getConnection();
							double weight = g.getWeight();	
							double value = n.getValue();
							g.setLastInput(connect.getValue()+value*weight);
							connect.setValue(connect.getValue()+value*weight);
							g.getConnection().addInput(g);
							g.setInput(n);
							
						}
						n.setLast(n.getValue());
						n.setValue(0.0000001);
					}
				}
				else if (l.isOutput()){
					//calls the output methods if the data that passes all the way through is enough to trigger the output neuron.
					for (Neuron n : l.getNeurons()){
						if (Backpropagate.Sigmoid(n.getValue()) > Double.parseDouble(PropertyReader.getProperty("activation"))){
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
							g.getConnection().addInput(g);
							g.setInput(n);
						}
						n.setLast(n.getValue());
						n.setValue(0.01);
					}
				}
			}
			
		}				
	@SuppressWarnings({ "deprecation", "unchecked"})
	//save method
	private static void save(Singleton s) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		NeuralNetwork[] nns = s.getNetworks();
		String type = PropertyReader.getProperty("type");
		Class<? extends SpecialNetManager> class1 = (Class<? extends SpecialNetManager>) Class.forName("BackEvolution." + type +"."+ type +"NetManager");
		SpecialNetManager manager = class1.newInstance();
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
					if (l.isInput())neurondata = manager.saveInput(n);
					else neurondata = "" + n.getNumber();
					for (Gene g :n.getGenes()){
						geneSave(manager, fout, nn, layernumber, n, neurondata, g);
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
	private static void geneSave(SpecialNetManager manager, PrintWriter fout, NeuralNetwork nn, int layernumber,
			Neuron n, String neurondata, Gene g) {
		Neuron nout = g.getConnection();
		String noutnum = null;
		if (nn.getLayers().size() == nout.getLayernumber()) {
			if(nout!=null)noutnum = manager.saveOutput(nout);
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
