package General;
/**
 * NetworkCreator.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public abstract class NetworkCreator {
	public static Random rand = new Random();
	@SuppressWarnings({ "unchecked", "resource" })
	public static NeuralNetwork[] CreateNetworks(Singleton s){
		//if there are no load files , it creates one random gene for each neural network.
		try {
			String type = PropertyReader.getProperty("type");
			Class<? extends NeuralNetwork> NetClass = (Class<? extends NeuralNetwork>) Class.forName("BackEvolution."+type+"."+type+"Network");
			NeuralNetwork[] NetworkList = new NeuralNetwork[Integer.parseInt(PropertyReader.getProperty("numNetworks"))];
			Class<? extends SpecialCreator> CreatorClass = (Class<? extends SpecialCreator>) Class.forName("BackEvolution."+type+"."+type+"Creator");
			@SuppressWarnings("deprecation")
			SpecialCreator creator = CreatorClass.newInstance();
			try {
				for (int j = 0; j<Integer.parseInt(PropertyReader.getProperty("numNetworks"));j++){		
					createNetwork(s, NetClass, NetworkList, creator, j);	
				}
			}	 
			catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | IOException | InstantiationException | IllegalAccessException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
			try{
				File file = new File("Generation.txt");
				Scanner fin = new Scanner(file);
				for (NeuralNetwork nn : NetworkList){
					String[] netData = fin.nextLine().split(";");
					load(s, nn, netData);
				}
			}
			catch(FileNotFoundException e){
				for (NeuralNetwork nn : NetworkList){
					Evolve.Mutate.newGeneInsert(nn, s);
				}
			}
			return NetworkList;
		}
		catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	private static void createNetwork(Singleton s, Class<? extends NeuralNetwork> NetClass, NeuralNetwork[] NetworkList,
		SpecialCreator creator, int j) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
		InvocationTargetException, IOException, InstantiationException {
		Layer[] layers = creator(s);
		creator.InputOutputcreator(layers);
		Class<?>[] types = {Layer.class,Layer.class};
		Constructor<? extends NeuralNetwork> con = NetClass.getConstructor(types);
		NetworkList[j] = (NeuralNetwork) con.newInstance(layers[0],layers[1]);
		s.setNetworks(NetworkList);
		for (Neuron no : layers[1].getNeurons()){
			creator.NeuronSetup(no, j);
		}
	}
	//Generates a single random gene
	public static Layer[] RandomGenerate(Layer[] layers, Singleton s){
		Layer outputlayercopy = layers[1];
		Layer inputlayercopy = layers[0];
		outputlayercopy.setNumber(2);
		inputlayercopy.setNumber(1);
		int inputrand = 0;
		if(inputlayercopy.getNeurons().size()-1 >0) inputrand = rand.nextInt(inputlayercopy.getNeurons().size()-1);
		int outputrand = 0;
		if(outputlayercopy.getNeurons().size()-1 >0) outputrand = rand.nextInt(outputlayercopy.getNeurons().size()-1);
		Gene starter = new Gene(outputlayercopy.getNeurons().get(outputrand), (Math.random()*2)-1, s);
		inputlayercopy.getNeurons().get(inputrand).AddGenes(starter);
		starter.setInput(inputlayercopy.getNeurons().get(inputrand));
		Layer[] copies = {inputlayercopy, outputlayercopy};
		return copies;
	}
	//Adds a gene to a neuron
	public static void geneAdder(double weight, int inLayer, int outLayer, Integer inNeuron, Integer outNeuron, NeuralNetwork nn, long id){
			Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1), weight, id);
			nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1).AddGenes(g2);
			g2.setInput(nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1));
			nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1).addInput(g2);
		}
	@SuppressWarnings({ "unchecked", "deprecation" })
	//loads a NeuralNetwork from a file
	public static void load(Singleton s, NeuralNetwork nn, String[] netData) throws InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException{
			String type = PropertyReader.getProperty("type");
			Class<? extends Neuron> class1 = (Class<? extends Neuron>) Class.forName("BackEvolution."+type+"."+type+"Neuron");
			Class<? extends Layer> class2 = (Class<? extends Layer>) Class.forName("BackEvolution."+type+"."+type+"Layer");
			Class<? extends SpecialNetManager> managerClass = (Class<? extends SpecialNetManager>) Class.forName("BackEvolution."+type+"."+type+"NetManager");
			SpecialNetManager manager = managerClass.newInstance();							
			loadLayers(nn, class2, netData);
			loadNeurons(nn, class1, netData);
			Neuraltracker(nn);
			loadGenes(nn, manager, netData);
		}
	private static void loadGenes(NeuralNetwork nn, SpecialNetManager manager, String[] netData) {
			String[] GeneData = netData[2].split(",");
			for(String data : GeneData){
				if(!data.equals("")){
					String[] g = data.split(":");
					int inLayer = Integer.parseInt(g[0]);
					Integer inNeuron = setInput(nn, manager, g, inLayer);
					int outLayer = Integer.parseInt(g[3]);
					Integer outNeuron = setOutput(nn, manager, netData, g, outLayer);
					double weight = Double.parseDouble(g[4]);
					long id = Long.parseLong(g[5]);
					if(inNeuron != null && outNeuron != null){
						NetworkCreator.geneAdder(weight,inLayer,outLayer,inNeuron,outNeuron,nn,id);						
					}						
				}
			}
		}
	private static Integer setOutput(NeuralNetwork nn, SpecialNetManager manager, String[] netData, String[] g,
			int outLayer) {
		Integer outNeuron = null;
		if (outLayer == Integer.parseInt(netData[0])){
			for (Neuron n : nn.getLayers().get(Integer.parseInt(netData[0])-1).getNeurons()){
				if (manager.saveOutput(n).equals(g[2])){
					outNeuron = n.getNumber();
				}
			}
		}
		else{
			outNeuron = Integer.parseInt(g[2]);
		}
		return outNeuron;
	}
	private static Integer setInput(NeuralNetwork nn, SpecialNetManager manager, String[] g, int inLayer) {
		Integer inNeuron = null;
		if (inLayer == 1){
			for (Neuron n : nn.getLayers().get(0).getNeurons()){
				if (manager.saveInput(n).equals(g[1])){
					inNeuron = n.getNumber();
				}	
			}
		}				
		else{
			try{
				inNeuron = Integer.parseInt(g[1]);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return inNeuron;
	}
	@SuppressWarnings("deprecation")
	private static void loadNeurons(NeuralNetwork nn, Class<? extends Neuron> class1, String[] netData)
				throws InstantiationException, IllegalAccessException {
			String[] NeuronData = netData[1].split(",");
			for (int i =1; i < nn.getLayers().size()-1; i++){
				for (int j = 0; j < Integer.parseInt(NeuronData[i]); j++){
					nn.getLayers().get(i).addNeuron(class1.newInstance());
				}
			}
		}
	@SuppressWarnings("deprecation")
	private static void loadLayers(NeuralNetwork nn, Class<? extends Layer> class2, String[] netData)
				throws InstantiationException, IllegalAccessException {
			for (int i = 2; i < Integer.parseInt(netData[0]); i++){
				Layer l = class2.newInstance();
				l.setInput(false);
				l.setOutput(false);
				nn.addLayer(l);
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
	//builds the input and output layers for each neural network
		@SuppressWarnings({ "unchecked", "deprecation" })
	public static Layer[] creator(Singleton s) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, InstantiationException{
			String type = PropertyReader.getProperty("type");
			Class<? extends Layer> class1 = (Class<? extends Layer>) Class.forName("BackEvolution."+type+"."+type+"Layer");
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



