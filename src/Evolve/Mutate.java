package Evolve;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

import General.DataManager;
import General.Properties;
import General.PropertyReader;
import NeuralNetwork.Gene;
import NeuralNetwork.Layer;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.Neuron;


public class Mutate {
	public Random rand;
	private DataManager data;
	public Mutate(DataManager data) {
		this.data = data;
		rand = new Random();
	}
	public NeuralNetwork mutate(NeuralNetwork newnn){
		double mutationSelector = Math.random();
		ArrayList<Gene> genes = geneArrayCreator(newnn);
		if (mutationSelector < Double.parseDouble(PropertyReader.getProperty(Properties.DISABLE.toString()))&& genes.size() > 0)geneMutation(mutationSelector, genes);
		else if (mutationSelector < Double.parseDouble(PropertyReader.getProperty(Properties.NEW_GENE.toString())) || genes.size() == 0)newGeneInsert(newnn);
		else {
			if (newnn.getLayers().size() == 2 || mutationSelector > Double.parseDouble(PropertyReader.getProperty(Properties.EXISTING_LAYER.toString())))newNeuronInNewLayer(newnn, genes);	
			else newNeuronInExistingLayer(newnn);	
		}
		return newnn;
	}
	public ArrayList<Gene> geneArrayCreator(NeuralNetwork newnn) {
		ArrayList<Gene> genes = new ArrayList<Gene>();
		for (Layer l : newnn.getLayers()){
				for (Neuron n : l.getNeurons()){						
					for (Gene g : n.getGenes())genes.add(g);					
				}
		}
		return genes;
	}
	@SuppressWarnings({ "unchecked"})
	public void newNeuronInExistingLayer(NeuralNetwork newnn){
		String type = PropertyReader.getProperty(Properties.TYPE.toString());
		try {
			Class<? extends Neuron> neuronClass = (Class<? extends Neuron>) Class.forName("BackEvolution."+type+"."+type+"Neuron");
			//new node in existing layer
			ArrayList<Layer> layers = new ArrayList<Layer>();
			for (Layer l : newnn.getLayers()){
				if (!l.isInput()&&!l.isOutput()){
					layers.add(l);
				}
			}
			
			Layer selected = null;
			try{
				selected = layers.get(rand.nextInt(layers.size()-1));
			}
			catch(Exception e){
				selected = layers.get(0);
			}
			ArrayList<Gene> genes2 = new ArrayList<Gene>();
			for (int i = 0 ; i < selected.getNumber()-1; i++){
				Layer l = newnn.getLayers().get(i);	
				for (Neuron n : l.getNeurons()){
					for(Gene g : n.getGenes()){
						if (g.getConnection().getLayernumber() > selected.getNumber() && n.getLayernumber() < selected.getNumber())genes2.add(g);
					}
				}
			}				
			try{
				Gene gene = null;
				try{
					gene = genes2.get(rand.nextInt(genes2.size()-1));
				}
				catch(Exception e){
					gene = genes2.get(0);
				}
				Neuron n = neuronClass.newInstance();
				
				n.setLayernumber(selected.getNumber());				
				selected.addNeuron(n);
				n.setNumber(selected.getNeurons().size());
				Neuron out = gene.getConnection();
				gene.setConnection(n);
				Gene gene2 = new Gene(out, Math.random()*2-1, data);
				n.AddGenes(gene2);
				gene2.setInput(n);
			}
			catch(Exception e){
				ArrayList<Neuron> ns = newnn.getLayers().get(0).getNeurons();
				Neuron in = null;
				try{
					in = ns.get(rand.nextInt(ns.size()-1));
				}
				catch(Exception f){
					in = ns.get(0);
				}
				ArrayList<Neuron> ns2 = ((Layer) newnn.getLayers().get(newnn.getLayers().size()-1)).getNeurons();
				Neuron on = null;
				try{
					on = ns2.get(rand.nextInt(ns2.size()-1));
				}
				catch(Exception f){
					on = ns2.get(0);
				}
				Neuron n = neuronClass.newInstance();
				Gene gene1 = new Gene(n, Math.random()*2-1, data);
				in.AddGenes(gene1);
				gene1.setInput(in);
				Gene gene2 =new Gene(on,Math.random()*2-1, data);
				gene2.setInput(n);
				n.AddGenes(gene2);
				selected.addNeuron(n);
				n.setNumber(selected.getNeurons().size());
				n.setLayernumber(selected.getNumber());
			}
		}
		catch (ClassNotFoundException|InstantiationException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	@SuppressWarnings({ "unchecked" })
	public void newNeuronInNewLayer(NeuralNetwork newnn, ArrayList<Gene> genes) {
		//new neuron in new layer
		String type = PropertyReader.getProperty(Properties.TYPE.toString());
		
		try {
			Class<? extends Neuron> neuronClass = (Class<? extends Neuron>) Class.forName("BackEvolution."+type+"."+type+"Neuron");
			Class<? extends Layer> layerClass = (Class<? extends Layer>) Class.forName("BackEvolution."+type +"."+type+"Layer");
			Class<?>[] types = {boolean.class,boolean.class};
			Constructor<? extends Layer> con = layerClass.getConstructor(types);
			Layer l = con.newInstance(false,false);

			newnn.addLayer(l);
			l.setNumber(newnn.getLayers().size()-1);
			Neuron n = neuronClass.newInstance();
			l.addNeuron(n);
			n.setNumber(1);
			n.setLayernumber(newnn.getLayers().size()-1);
			Layer outputlayer = newnn.getLayers().get(newnn.getLayers().size()-1);
			outputlayer.setNumber(newnn.getLayers().size());

			
			ArrayList<Gene> genes2 = new ArrayList<Gene>();
			for (Gene g : genes){
				if (newnn.getLayers().get(g.getConnection().getLayernumber()-1).isOutput()) genes2.add(g);
			}
			if (genes2.size() > 0){
				Gene gene= genes2.get((int) ((genes2.size()-1)*Math.random()));
				Neuron out = gene.getConnection();
				gene.setConnection(n);
				n.AddGenes(new Gene(out, Math.random()*2 -1, data));
				gene.setInput(n);
			}
			else{
				ArrayList<Neuron> ns  = new ArrayList<Neuron>();
				for (Gene g : genes){
					ns.add(g.getConnection());
				}
				Gene gene = new Gene(n, Math.random()*2-1, data);
				n.addInput(gene);
				Gene gene2 = new Gene(outputlayer.getNeurons().get(0), Math.random()*2-1, data);
				if(outputlayer.getNeurons().size()-1 > 0) gene2 = new Gene(outputlayer.getNeurons().get(rand.nextInt(outputlayer.getNeurons().size()-1)), Math.random()*2-1, data);
				try{
					Neuron in = ns.get(rand.nextInt(ns.size()-1));
					in.AddGenes(gene);
					gene.setInput(in);
					gene.getConnection().addInput(gene);
				}
				catch(Exception e){
					ns.get(0).AddGenes(gene);
					gene.setInput(ns.get(0));
					gene.getConnection().addInput(gene);
				}
				n.AddGenes(gene2);
				gene2.setInput(n);
				gene2.getConnection().addInput(gene2);
			}
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	public void newGeneInsert(NeuralNetwork newnn) {
		// new gene
		int layer = 0;
		if (newnn.getLayers().size() > 2) layer = rand.nextInt(newnn.getLayers().size()-2);
		int neuron = 0;
		if (newnn.getLayers().get(layer).getNeurons().size() > 1){
				neuron = rand.nextInt(newnn.getLayers().get(layer).getNeurons().size()-1);
		}
		int layer2 = newnn.getLayers().size()-1;
		if (newnn.getLayers().size()-2-layer > 0) layer2 = layer + 1 + rand.nextInt(newnn.getLayers().size()-2-layer);
		int neuron2 = 0;
		if (newnn.getLayers().get(layer2).getNeurons().size() > 1){
				neuron2 =  rand.nextInt(newnn.getLayers().get(layer2).getNeurons().size()-1);
		}
		double weight = Math.random()*2 - 1;
		Neuron in = newnn.getLayers().get(layer).getNeurons().get(neuron);
		Gene g = new Gene(newnn.getLayers().get(layer2).getNeurons().get(neuron2), weight, data);
		newnn.getLayers().get(layer2).getNeurons().get(neuron2).addInput(g);
		for(int i = 0; i < in.getGenes().size();i++){
			if (newnn.getLayers().get(layer).getNeurons().get(neuron).getGenes().get(i).getConnection().getLayernumber() == layer2 &&newnn.getLayers().get(layer).getNeurons().get(neuron).getGenes().get(i).getConnection().getNumber() == neuron2){
				in.RemoveGenes(in.getGenes().get(i));
				i--;
			}
		}
		in.AddGenes(g);
	}
	private void geneMutation(double selector, ArrayList<Gene> genes) {
		Gene gene= genes.get(0);
		if(genes.size() > 1) gene= genes.get(rand.nextInt(genes.size()-1));
		if(selector < Double.parseDouble(PropertyReader.getProperty(Properties.ADJUST.toString()))){
			//adjust weight
			
		}
		else if (selector < Double.parseDouble(PropertyReader.getProperty(Properties.RANDOM.toString()))){
			// new random weight
			randomWeight(gene);
			
		}
		else{
			//disable/enable gene
			geneDisable(gene);
		}
	}
	public void geneDisable(Gene gene) {
		gene.remove();
	}
	public void randomWeight(Gene gene) {
		gene.setWeight(Math.random()*2 - 1);
	}
	public void geneAdjust(Gene gene) {
		gene.setWeight(gene.getWeight()*(1+(Math.random()*.2-.1)));
	}
}
