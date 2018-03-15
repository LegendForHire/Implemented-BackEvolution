package BackEvolution;
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
public abstract class NeuralNetManager {
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void start(Singleton s) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, InstantiationException {
		// TODO Auto-generated method stub
		Class<? extends SpecialNetManager> class1 = (Class<? extends SpecialNetManager>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"NetManager");
		SpecialNetManager netManager = class1.newInstance();
		netManager.setup();
		NeuralNetwork[] nns =  NetworkCreator.CreateNetworks(s); 
		s.setNetworks(nns);
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
	@SuppressWarnings({ "unchecked", "deprecation" })
	protected static void RunNetworks(Singleton s) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, InterruptedException, InstantiationException {		
			Class<? extends SpecialNetManager> class1 = (Class<? extends SpecialNetManager>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"NetManager");
			SpecialNetManager netManager = class1.newInstance();
			while(true) {
			System.out.println("Iteration " + s.getGen());
			s.getWriter().println("Iteration" + s.getGen());
			NeuralNetwork[] nns = s.getNetworks();
			double scaling = Math.log(s.getGen())*3+1;
			s.setTotalGlobalError(s.getAllowedError()/scaling + 1);
			
			// see method description
			for(NeuralNetwork nn : nns) {
				Neuraltracker(nn);	
			}
			// this is where the back propagation learning step for the neural networks run. currently I have them set to run for one minute before evaluating
			while(s.getTotalGlobalError() > s.getAllowedError()/scaling){
				//set old values for back propagation step
				netManager.BackIterationHandling();
				for (NeuralNetwork nn : nns){
					RunNetwork(nn,s);					
				}
				long t1 = System.currentTimeMillis();
				while(System.currentTimeMillis() - t1 < s.getTiming());
				Backpropagate.backpropagate(nns,s);		
				s.getWriter().println("Total Global Error:" + s.getTotalGlobalError()); 
				s.getWriter().println("backpropagation complete");
			}
		//Just so it's easy to keep track of how well things are doing all of the wallets are restarted to a default state at the beginning of each run
		netManager.EvolveSetup();
		long t1 = System.currentTimeMillis();
		while (System.currentTimeMillis()-t1 < s.getTiming()){
			for (NeuralNetwork nn : nns){
				RunNetwork(nn,s);					
			}	
		}
		
		//see method description
		for(NeuralNetwork nn : nns) {
			Neuraltracker(nn);	
		}
		netManager.EvolveTeardown();
		// saves the current state of the neural networks.
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
						if (Backpropagate.Sigmoid(n.getValue()) > s.getActivation()){
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
	@SuppressWarnings({ "deprecation" })
	private static void save(Singleton s) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		NeuralNetwork[] nns = s.getNetworks();
		@SuppressWarnings("unchecked")
		Class<? extends SpecialNetManager> class1 = (Class<? extends SpecialNetManager>) Class.forName("BackEvolution." + s.getType()+"."+s.getType()+"NetManager");
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
				if (l.isInput()){
					int layernumber = l.getNumber();
					for (Neuron n : l.getNeurons()){
						String neurondata = manager.saveInput(n);
						for (Gene g :n.getGenes()){
							Neuron nout = g.getConnection();
							if (nn.getLayers().size() == nout.getLayernumber()) {
								Neuron nout2 = g.getConnection();
								String noutnum = manager.saveOutput(nout2);
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
								Neuron nout2 = g.getConnection();
								if(nout2 == null){
									n.RemoveGenes(g);
								}
								else{
								String noutnum = manager.saveOutput(nout2);
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
			fout.println();
		}
		fout.close();
		}
		catch(Exception e) {
			e.printStackTrace();
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
