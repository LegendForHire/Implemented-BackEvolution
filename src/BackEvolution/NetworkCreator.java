package BackEvolution;
/**
 * NetworkCreator.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public abstract class NetworkCreator {
	public static Random rand = new Random();
	@SuppressWarnings("unchecked")
	public static NeuralNetwork[] CreateNetworks(Singleton s) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		//if there are no load files , it creates one random gene for each neural network.
			Class<? extends NeuralNetwork> NetClass = (Class<? extends NeuralNetwork>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"Network");
			NeuralNetwork[] NetworkList = new NeuralNetwork[s.getNumNetworks()];
			Class<? extends SpecialCreator> CreatorClass = (Class<? extends SpecialCreator>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"Creator");
			SpecialCreator creator = CreatorClass.newInstance();
		
		try{
			for (NeuralNetwork nno : NetworkList){
			File file = new File("Generation.txt");					
			Scanner fin = new Scanner(file);
			creator.load(nno, regularLoad(s, nno, fin));
			s.getWriter().println("loaded Networks");
			}
		}
		catch(Exception e){
			s.getWriter().println("Creating Networks from Scratch");
		for (int j = 0; j<s.getNumNetworks();j++){		
				Layer[] layers;
				try {
					layers = creator(s);
					creator.InputOutputcreator(layers);
					Layer[] copies = RandomGenerate(layers);
					Class<?>[] types = {Layer.class,Layer.class};
					Constructor<? extends NeuralNetwork> con = NetClass.getConstructor(types);
					NetworkList[j] = (NeuralNetwork) con.newInstance(copies[0],copies[1]);
					s.setNetworks(NetworkList);
					for (Neuron no : copies[1].getNeurons()){
						creator.NeuronSetup(no, j);
					}
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException | InstantiationException
						| IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.exit(1);
				}
				
				}									
		}
		return NetworkList;		
	}
		//loads the most recent save file.
		public static Layer[] RandomGenerate(Layer[] layers){
			Layer outputlayercopy = layers[1];
			Layer inputlayercopy = layers[0];
			outputlayercopy.setNumber(2);
			inputlayercopy.setNumber(1);
			int inputrand = 0;
			if(inputlayercopy.getNeurons().size()-1 >0) inputrand = rand.nextInt(inputlayercopy.getNeurons().size()-1);
			int outputrand = 0;
			if(outputlayercopy.getNeurons().size()-1 >0) outputrand = rand.nextInt(outputlayercopy.getNeurons().size()-1);
			Gene starter = new Gene(outputlayercopy.getNeurons().get(outputrand), (Math.random()*2)-1);
			inputlayercopy.getNeurons().get(inputrand).AddGenes(starter);
			starter.setInput(inputlayercopy.getNeurons().get(inputrand));
			Layer[] copies = {inputlayercopy, outputlayercopy};
			return copies;
		}
		public static void geneAdder(double weight, int inLayer, int outLayer, Integer inNeuron, Integer outNeuron, NeuralNetwork nn, int enabled){
			Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1), weight);
			if (enabled == 0)g2.toggle();
			nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1).AddGenes(g2);
			g2.setInput(nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1));
			nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1).addInput(g2);
		}
		@SuppressWarnings({ "unchecked" })
		public static String[] regularLoad(Singleton s, NeuralNetwork nn, Scanner fin) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
			Class<? extends Neuron> class1 = (Class<? extends Neuron>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"Neuron");
			Class<? extends Layer> class2 = (Class<? extends Layer>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"Layer");
			String[] netData = fin.nextLine().split(";");
			// creates a layer based on the numlayer data point
			for (int i = 2; i < Integer.parseInt(netData[0]); i++){
				Layer l = class2.newInstance();
				l.setInput(false);
				l.setOutput(false);
				nn.addLayer(l);
			}
			//creates neurons in each layer
			String[] NeuronData = netData[1].split(",");
			for (int i =1; i < nn.getLayers().size()-1; i++){
				for (int j = 0; j < Integer.parseInt(NeuronData[i]); j++){
					nn.getLayers().get(i).addNeuron(class1.newInstance());
				}
			}
			//creates the genes and adds them to each neuron.
			Neuraltracker(nn);
			return netData;
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
		//builds the input and output layers for each neural network
		@SuppressWarnings({ "unchecked" })
		public static Layer[] creator(Singleton s) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, InstantiationException{
			Class<? extends Layer> class1 = (Class<? extends Layer>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"Layer");
			Layer[] layers = new Layer[2];	
			layers[0] = class1.newInstance();
			layers[0].setInput(true);
			layers[0].setOutput(false);
			layers[0].setNumber(1);
			layers[1] = class1.newInstance();
			layers[1].setOutput(true);
			layers[1].setInput(false);
			layers[0].setNumber(2);
			return layers;
		}
}



