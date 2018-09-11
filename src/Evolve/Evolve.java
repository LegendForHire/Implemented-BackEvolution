package Evolve;
/**
 * Evolve.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import Competitive.Competition;
import Competitive.CompetitionSingleton;
import General.Gene;
import General.Layer;
import General.NetworkCreator;
import General.NeuralNetManager;
import General.NeuralNetwork;
import General.Neuron;
import General.Singleton;
import General.SpecialCreator;

public class Evolve {
	private static final double CLONE_CHANCE = .25;
	public static Random rand = new Random();
	
	public static void runner(Singleton s1) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, InstantiationException, ClassNotFoundException, InterruptedException {
		EvolveSingleton s = (EvolveSingleton) s1;
		EvolveManager netManager = netManagerReflected(s);
		netManager.EvolveSetup();
		NeuralNetwork[] nns = s.getNetworks();
		long t1 = System.currentTimeMillis();
		// runs the networks for a minute to measure their performance
		if(s.numCompeting() > 1){
			Competition.evolutionRunner((CompetitionSingleton) s);
		}
		else{
			while (System.currentTimeMillis()-t1 < s.getTiming()){
				for (NeuralNetwork nn : nns){
					NeuralNetManager.RunNetwork(nn,s);					
				}	
			}
		}
		//see method description
		for(NeuralNetwork nn : nns) {
			NeuralNetManager.Neuraltracker(nn);	
		}
		netManager.EvolveTeardown();
		
	}
	private static EvolveManager netManagerReflected(EvolveSingleton s)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		Class<? extends EvolveManager> class1 = (Class<? extends EvolveManager>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"NetManager");
		@SuppressWarnings("deprecation")
		EvolveManager netManager = class1.newInstance();
		return netManager;
	}
	public static NeuralNetwork[] evolve(NeuralNetwork[] nns,Singleton s1) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		EvolveSingleton s = (EvolveSingleton) s1;
		Arrays.sort(nns);
		NeuralNetwork[] halfnns = new NeuralNetwork[nns.length/2];
		//This will be the new population that is returned
		NeuralNetwork[] newnns = new NeuralNetwork[nns.length];
		// this keeps track of how many survivors were artifically selected for survival.
		int tracker = survivorSelection(nns, halfnns);		
		//adds in the rest of the survivors strictly based on merit
		Double totalFitness = fitnessSetup(nns, halfnns, newnns, tracker);
		//this is where new neural networks are born for the new population
		for (int i = nns.length/2; i<nns.length; i++){
			newNetworkAtLoc(s, halfnns, newnns, totalFitness, i);
		}
		
		return newnns;
		
	}
	private static int survivorSelection(NeuralNetwork[] nns, NeuralNetwork[] halfnns) {
		int maxLayers = 2;
		int tracker = 0;
		//finds the most layers among all the neural networks
		for(NeuralNetwork nn : nns){
			nn.setAge(nn.getAge()+1);
			ArrayList<Layer> l = nn.getLayers();
			if (l.size() > maxLayers){
				maxLayers = l.size();
			}
		}
		//will be changed to true speciation later. pointless as is.
		// for each # of layers keep the best 1 percent alive for a few generations in to give time for the new layer to optimize. Might remove as it seems to artifcially select for bigger neural networks and organic growth might be better;.
		for (int i = maxLayers; i > 2; i--){
			int j = nns.length/100;
			int k = 0;
			while (j>0 && k < nns.length-1){
				if (nns[k].getLayers().size() == i && nns[k].getAge()<6){
					j--;
					halfnns[tracker] = nns[k];
					tracker++;
				}
				k++;
				
			}
		}
		return tracker;
	}
	private static void newNetworkAtLoc(EvolveSingleton s, NeuralNetwork[] originalPopulation, NeuralNetwork[] newnns, Double totalFitness, int loc) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
		InvocationTargetException, IOException, InstantiationException {
		//this random decides if the network will be cloned or bred.
		Double cloneVsCrossover=  Math.random();
		//cloned
		if (cloneVsCrossover <= CLONE_CHANCE){
			NeuralNetwork clonedParent = parentSelection(originalPopulation, totalFitness);
			newnns[loc] = clone(clonedParent, s);
		}
		//crossover
		else{
			NeuralNetwork parent1 = parentSelection(originalPopulation, totalFitness);
			ArrayList<NeuralNetwork> equalLayers = new ArrayList<NeuralNetwork>();
			//creates an array of neural networks with the same number of layers as the parent otherwise breeding is difficult
			Double equalFitness = 0.0;
			for(NeuralNetwork nn:originalPopulation){
				if (nn.getLayers().size() == parent1.getLayers().size()){
					equalLayers.add(nn);
					equalFitness += nn.getFitness();
				}
			}
			//selects from the previously created array
			NeuralNetwork parent2 = parentSelection((NeuralNetwork[]) equalLayers.toArray(),equalFitness);
			//breeds
			newnns[loc] = crossover(parent1,parent2,s);
		}
	}
	private static NeuralNetwork parentSelection(NeuralNetwork[] originalPopulation, Double totalFitness) {
		//this decides which network it will be a clone of
		Double who = Math.random()*totalFitness;
		NeuralNetwork cloner = null;
		boolean selected = false;
		for (NeuralNetwork nn : originalPopulation){
			who = who -nn.getFitness();
			if (who <= 0 && !selected){
				cloner = nn;
				selected = true;
			}
		}
		if (cloner == null){
			cloner = originalPopulation[99];
		}
		return cloner;
	}
	private static Double fitnessSetup(NeuralNetwork[] nns, NeuralNetwork[] halfnns, NeuralNetwork[] newnns, int tracker) {
		int modifier = 0 + tracker;
		for (int i = tracker; i < halfnns.length; i++){
			//makes sure duplicates aren't added.
			for (int j = 0; j < tracker; j++){
				if(halfnns[j] != null){
					if(halfnns[j] == halfnns[i]){
						modifier--;
					}
				}
			}
			halfnns[i] = nns[i - modifier];
			
		}//sorts the new array so that its ordered again from most fit to least fit;
		Arrays.sort(halfnns);
		//creates a sum of the total fitness of all networks for weighted random selection, also adds the survivors tothe new new population;
		Double totalFitness = 0.0;
		for (int i = 0; i < halfnns.length; i++){
			newnns[i] = halfnns[i];
			totalFitness += halfnns[i].getFitness();
		}
		return totalFitness;
	}
	private static NeuralNetwork clone(NeuralNetwork cloner, Singleton s1) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, InstantiationException {
		EvolveSingleton s = (EvolveSingleton) s1;
		//starts by creating the basic structure for the new network
		NeuralNetwork newnn = newNetwork(s);
		ArrayList<Layer> clonelayers = cloner.getLayers();
		//adds layer and neuron structure
		for (int i = 1; i < clonelayers.size()-1; i++){
				ArrayList<Neuron> ns = clonelayers.get(i).getNeurons();
				networkStructureCreation(newnn, ns.size(), s);					
		}
		layerTrackingReset(clonelayers);
		//adds genes to structure
		ArrayList<double[]> geneIdentities = getGeneIdentities(clonelayers);
		geneAdder(newnn, geneIdentities);
		//returns a mutated clone
		return mutate(newnn,s);
	}
	private static NeuralNetwork crossover(NeuralNetwork cross, NeuralNetwork over, Singleton s1) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		EvolveSingleton s = (EvolveSingleton) s1;
		NeuralNetwork newnn = newNetwork(s);
		NeuralNetwork lessfit = null;
		NeuralNetwork morefit = null;
		if (cross.getFitness() > over.getFitness()){
			morefit = cross;
			lessfit = over;
		}
		else{
			morefit = over;
			lessfit = cross;
		}
		//pretty sure this is obsolete from when i tried to breed different layered networks but not sure if I can delete.
		ArrayList<Layer> lesslayers = lessfit.getLayers();
		ArrayList<Layer> morelayers = morefit.getLayers();
		int maxlayers = 1;
		if (lesslayers.size() > morelayers.size()){
			maxlayers = lesslayers.size();
		}
		else { 
			maxlayers = morelayers.size();
		}
		//adds the neuron to each layer based on which network had the most neurons in that layer
		for (int i = 1; i < maxlayers-1; i++){
			int numNeurons = getNumNeurons(lesslayers, morelayers, i);
			networkStructureCreation(newnn, numNeurons, s);
		}
		layerTrackingReset(lesslayers);
		layerTrackingReset(morelayers);
		ArrayList<double[]> geneIdentities = getGeneIdentities(morelayers);
		ArrayList<double[]> geneIdentities2 = getGeneIdentities(lesslayers);
		geneBreeder(geneIdentities, geneIdentities2);
		geneAdder(newnn, geneIdentities);
		geneAdder(newnn, geneIdentities2);
		return mutate(newnn,s);
	}
	private static void geneBreeder(ArrayList<double[]> geneIdentities, ArrayList<double[]> geneIdentities2) {
		for (double[] nums : geneIdentities){
			for (int i = 0; i < geneIdentities2.size(); i++){
				double[] nums2 = geneIdentities2.get(i);
				if (nums[0] == nums2[0] && nums[1] == nums2[1] && nums[2] == nums2[2] && nums[3] == nums2[3]){
					nums[4] = (nums[4] + nums2[4])/2;
					geneIdentities2.remove(nums2);
					i--;
				}
			}
		}
	}
	private static void layerTrackingReset(ArrayList<Layer> morelayers) {
		for (int i = 1; i <=morelayers.size(); i++){
			Layer l = morelayers.get(i-1);
			for (Neuron n : l.getNeurons()){
				n.setLayernumber(i);
				
			}
			l.setNumber(i);	
		}
	}
	private static int getNumNeurons(ArrayList<Layer> lesslayers, ArrayList<Layer> morelayers, int i) {
		Layer lesslayer = null;
		Layer morelayer = null;
		int lessnum = 0;
		int morenum = 0;
		try{
		lesslayer = lesslayers.get(i);
		lessnum = lesslayer.getNeurons().size();
		}
		catch(Exception e){
			morelayer = morelayers.get(i);
			morenum = morelayer.getNeurons().size();
		}
		try{
		morelayer = morelayers.get(i);
		morenum = morelayer.getNeurons().size();
		}
		catch (Exception e){
		}
		if (morelayer == null) morenum = 0;
		else if (morelayer.isOutput()){
			morenum = 0;
		}
		if (lesslayer == null) lessnum = 0;
		else if (lesslayer.isOutput()){
			lessnum = 0;
		}
		int numNeurons = (morenum > lessnum) ? morenum : lessnum;
		return numNeurons;
	}
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static NeuralNetwork newNetwork(EvolveSingleton s) throws ClassNotFoundException, NoSuchMethodException,
	IllegalAccessException, InvocationTargetException, IOException, InstantiationException {
		Class<? extends NeuralNetwork> networkClass = (Class<? extends NeuralNetwork>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"Network");
		Layer[] puts = NetworkCreator.creator(s);
		Class<? extends SpecialCreator> managerClass = (Class<? extends SpecialCreator>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"Creator");
		SpecialCreator manager = managerClass.newInstance();
		manager.InputOutputcreator(puts);
		Class<?>[] types2 = {Class.forName("BackEvolution.Layer"),Class.forName("BackEvolution.Layer")};
		Constructor<? extends NeuralNetwork> con2 = networkClass.getConstructor(types2);
		NeuralNetwork newnn = con2.newInstance(puts[0],puts[1]);
		return newnn;
	} 
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static void networkStructureCreation(NeuralNetwork newnn, int numNeurons, EvolveSingleton s)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		Class<? extends Layer> layerClass = (Class<? extends Layer>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"Layer");
		Class<? extends Neuron> neuronClass = (Class<? extends Neuron>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"Neuron");	
		Class<?>[] types = {boolean.class,boolean.class};
		Constructor<? extends Layer> con = layerClass.getConstructor(types);
		Layer newl = con.newInstance(false, false);
		newnn.addLayer(newl);
		newl.setNumber(newnn.getLayers().size()-1);
		for (int j = 0; j < numNeurons ; j++){
			Neuron newn = neuronClass.newInstance();
			newl.addNeuron(newn);
			newn.setLayernumber(newl.getNumber());
			newn.setNumber(newl.getNeurons().size()-1);
		}
	}
	private static ArrayList<double[]> getGeneIdentities(ArrayList<Layer> layers) {
		ArrayList<double[]> geneIdentities = new ArrayList<double[]>();
		for(int k =1; k <=layers.size(); k++){
			Layer l = layers.get(k-1);			
			for (Neuron n: l.getNeurons()){				
				for(Gene g : n.getGenes()){
					double data[] = new double[5];
					data[0] = k;
					data[1] = n.getNumber();	
					data[2] = g.getConnection().getNumber();
					data[3] = g.getConnection().getLayernumber();
					data[4] = g.getWeight();
					geneIdentities.add(data);
				}
			}
		}
		return geneIdentities;
	}
	private static void geneAdder(NeuralNetwork newnn, ArrayList<double[]> geneIdentities) {
		for(double[] nums : geneIdentities){			
			Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getNeurons().get((int) nums[2]-1), nums[4]);
			Neuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getNeurons().get((int) nums[1]-1);
			newNeuron.AddGenes(newGene);
			newGene.setInput(newNeuron);
		}
	}
	private static NeuralNetwork mutate(NeuralNetwork newnn, Singleton s1) throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EvolveSingleton s = (EvolveSingleton) s1;
		NeuralNetManager.Neuraltracker(newnn);
		double mutationSelector = Math.random();
		ArrayList<Gene> genes = geneArrayCreator(newnn);
		if (mutationSelector < s.getDisableProbability()&& genes.size() > 0)geneMutation(s, mutationSelector, genes);
		else if (mutationSelector < s.getNewGeneProbability() || genes.size() == 0)newGeneInsert(newnn);
		else {
			if (newnn.getLayers().size() == 2 || mutationSelector > s.getExistingLayerProbability())newNeuronInNewLayer(newnn, genes, s);	
			else newNeuronInExistingLayer(newnn, s);	
		}
		return newnn;
	}
	private static ArrayList<Gene> geneArrayCreator(NeuralNetwork newnn) {
		ArrayList<Gene> genes = new ArrayList<Gene>();
		for (Layer l : newnn.getLayers()){
				for (Neuron n : l.getNeurons()){						
					for (Gene g : n.getGenes())genes.add(g);					
				}
		}
		return genes;
	}
	@SuppressWarnings({ "deprecation", "unchecked" })
	private static void newNeuronInExistingLayer(NeuralNetwork newnn, EvolveSingleton s) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class<? extends Neuron> neuronClass = (Class<? extends Neuron>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"Neuron");
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
			Gene gene2 = new Gene(out, Math.random()*2-1);
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
			Gene gene1 = new Gene(n, Math.random()*2-1);
			in.AddGenes(gene1);
			gene1.setInput(in);
			Gene gene2 =new Gene(on,Math.random()*2-1);
			gene2.setInput(n);
			n.AddGenes(gene2);
			selected.addNeuron(n);
			n.setNumber(selected.getNeurons().size());
			n.setLayernumber(selected.getNumber());
		}
	}
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static void newNeuronInNewLayer(NeuralNetwork newnn, ArrayList<Gene> genes, EvolveSingleton s)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		//new neuron in new layer
		Class<? extends Neuron> neuronClass = (Class<? extends Neuron>) Class.forName("BackEvolution."+s.getType()+"."+s.getType()+"Neuron");
		Class<? extends Layer> layerClass = (Class<? extends Layer>) Class.forName("BackEvolution."+ s.getType()+"."+ s.getType()+"Layer");
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
			n.AddGenes(new Gene(out, Math.random()*2 -1));
			gene.setInput(n);
		}
		else{
			ArrayList<Neuron> ns  = new ArrayList<Neuron>();
			for (Gene g : genes){
				ns.add(g.getConnection());
			}
			Gene gene = new Gene(n, Math.random()*2-1);
			n.addInput(gene);
			Gene gene2 = new Gene(outputlayer.getNeurons().get(0), Math.random()*2-1);
			if(outputlayer.getNeurons().size()-1 > 0) gene2 = new Gene(outputlayer.getNeurons().get(rand.nextInt(outputlayer.getNeurons().size()-1)), Math.random()*2-1);
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
	}
	private static void newGeneInsert(NeuralNetwork newnn) {
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
		Gene g = new Gene(newnn.getLayers().get(layer2).getNeurons().get(neuron2), weight);
		newnn.getLayers().get(layer2).getNeurons().get(neuron2).addInput(g);
		for(int i = 0; i < in.getGenes().size();i++){
			if (newnn.getLayers().get(layer).getNeurons().get(neuron).getGenes().get(i).getConnection().getLayernumber() == layer2 &&newnn.getLayers().get(layer).getNeurons().get(neuron).getGenes().get(i).getConnection().getNumber() == neuron2){
				in.RemoveGenes(in.getGenes().get(i));
				i--;
			}
		}
		in.AddGenes(g);
	}
	private static void geneMutation(EvolveSingleton s, double selector, ArrayList<Gene> genes) {
		Gene gene= genes.get(0);
		if(genes.size() > 1) gene= genes.get(rand.nextInt(genes.size()-1));
		if(selector < s.getAdjustProbability()) {
			//adjust weight
			gene.setWeight(gene.getWeight()*(1+(Math.random()*.1)));
		}
		else if (selector < s.getRandomProbability()){
			// new random weight
			gene.setWeight(Math.random()*2 - 1);
			
		}
		else{
			//disable/enable gene
			gene.toggle();	
		}
	}
}
   