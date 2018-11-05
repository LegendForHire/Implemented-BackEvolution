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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import Evolve.Mutate;
import NeuralNetwork.Gene;
import NeuralNetwork.Layer;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.Neuron;

public abstract class NetworkCreator {
	public Random rand;
	protected DataManager data;
	private NeuralNetManager netManager;
	public NetworkCreator(DataManager data) {
		this.data = data;
		netManager = data.getNetManager();
		rand = new Random();
	}
	@SuppressWarnings({ "unchecked", "resource" })
	public NeuralNetwork[] CreateNetworks(){
		//if there are no load files , it creates one random gene for each neural network.
		try {
			String type = PropertyReader.getProperty(Properties.TYPE.toString());
			Class<? extends NeuralNetwork> NetClass = (Class<? extends NeuralNetwork>) Class.forName("BackEvolution."+type+"."+type+"Network");
			NeuralNetwork[] NetworkList = new NeuralNetwork[Integer.parseInt(PropertyReader.getProperty(Properties.NUM_NETWORKS.toString()))];
			try {
				for (int j = 0; j<Integer.parseInt(PropertyReader.getProperty(Properties.NUM_NETWORKS.toString()));j++){		
					createNetwork(NetClass, NetworkList, j);	
				}
			}	 
			catch (SecurityException | IllegalArgumentException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
			try{
				File file = new File("Generation.txt");
				Scanner fin = new Scanner(file);
				for (NeuralNetwork nn : NetworkList){
					String[] netData = fin.nextLine().split(";");
					load(nn, netData);
				}
			}
			catch(FileNotFoundException e){
				Mutate mutate = data.getMutate();
				for (NeuralNetwork nn : NetworkList){
					mutate.newGeneInsert(nn);
				}
			}
			return NetworkList;
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	private void createNetwork(Class<? extends NeuralNetwork> NetClass, NeuralNetwork[] NetworkList, int j){
		Layer[] layers = creator();
		InputOutputcreator(layers);
		Class<?>[] types = {Layer.class,Layer.class,DataManager.class};	
		try {
			Constructor<? extends NeuralNetwork> con = NetClass.getConstructor(types);
			NetworkList[j] = (NeuralNetwork) con.newInstance(layers[0],layers[1],data);
			data.setNetworks(NetworkList);
			for (Neuron no : layers[1].getNeurons()){
				NeuronSetup(no, j);
			}
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//Adds a gene to a neuron
	public void geneAdder(double weight, int inLayer, int outLayer, Integer inNeuron, Integer outNeuron, NeuralNetwork nn, long id){
			Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1), weight, id);
			nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1).AddGenes(g2);
			g2.setInput(nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1));
			nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1).addInput(g2);
		}
	@SuppressWarnings({ "unchecked"})
	//loads a NeuralNetwork from a file
	public void load(NeuralNetwork nn, String[] netData){
			String type = PropertyReader.getProperty(Properties.TYPE.toString());
			Class<? extends Neuron> class1;
			try {
				class1 = (Class<? extends Neuron>) Class.forName("BackEvolution."+type+"."+type+"Neuron");
				Class<? extends Layer> class2 = (Class<? extends Layer>) Class.forName("BackEvolution."+type+"."+type+"Layer");							
				loadLayers(nn, class2, netData);
				loadNeurons(nn, class1, netData);
				Neuraltracker(nn);
				loadGenes(nn, netData);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	private void loadGenes(NeuralNetwork nn, String[] netData) {
			String[] GeneData = netData[2].split(",");
			for(String data : GeneData){
				if(!data.equals("")){
					String[] g = data.split(":");
					int inLayer = Integer.parseInt(g[0]);
					Integer inNeuron = setInput(nn, g, inLayer);
					int outLayer = Integer.parseInt(g[3]);
					Integer outNeuron = setOutput(nn, netData, g, outLayer);
					double weight = Double.parseDouble(g[4]);
					long id = Long.parseLong(g[5]);
					if(inNeuron != null && outNeuron != null){
						geneAdder(weight,inLayer,outLayer,inNeuron,outNeuron,nn,id);						
					}						
				}
			}
		}
	private Integer setOutput(NeuralNetwork nn, String[] netData, String[] g,
			int outLayer) {
		Integer outNeuron = null;
		if (outLayer == Integer.parseInt(netData[0])){
			for (Neuron n : nn.getLayers().get(Integer.parseInt(netData[0])-1).getNeurons()){
				if (netManager.saveOutput(n).equals(g[2])){
					outNeuron = n.getNumber();
				}
			}
		}
		else{
			outNeuron = Integer.parseInt(g[2]);
		}
		return outNeuron;
	}
	private Integer setInput(NeuralNetwork nn, String[] g, int inLayer) {
		Integer inNeuron = null;
		if (inLayer == 1){
			for (Neuron n : nn.getLayers().get(0).getNeurons()){
				if (netManager.saveInput(n).equals(g[1])){
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
	private void loadNeurons(NeuralNetwork nn, Class<? extends Neuron> class1, String[] netData){
			String[] NeuronData = netData[1].split(",");
			for (int i =1; i < nn.getLayers().size()-1; i++){
				for (int j = 0; j < Integer.parseInt(NeuronData[i]); j++){
					try {
						nn.getLayers().get(i).addNeuron(class1.newInstance());
					} catch (InstantiationException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	private void loadLayers(NeuralNetwork nn, Class<? extends Layer> class2, String[] netData){
			for (int i = 2; i < Integer.parseInt(netData[0]); i++){
				Layer l;
				try {
					l = class2.newInstance();
					l.setInput(false);
					l.setOutput(false);
					nn.addLayer(l);
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}		
			}
		}
	//Makes sure neuron and gene location data are correct.
	public void Neuraltracker(NeuralNetwork nn){
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
	@SuppressWarnings("unchecked")
	public Layer[] creator(){
			String type = PropertyReader.getProperty(Properties.TYPE.toString());		
			try {
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
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
			return null;
			
		}
	//after neurons are created, use this to give them any additional data you might need to feed them
	public abstract void NeuronSetup(Neuron no, int j);
	//creates your input and output neurons on each startup
	//copies[0] is used for input neurons and copies[1] is used for output neurons
	public abstract void InputOutputcreator(Layer[] copies);
}



