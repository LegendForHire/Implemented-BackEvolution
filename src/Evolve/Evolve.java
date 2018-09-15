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
import java.util.HashMap;
import Competitive.Competition;
import Competitive.CompetitionSingleton;
import General.Gene;
import General.Layer;
import General.NetworkCreator;
import General.NeuralNetManager;
import General.NeuralNetwork;
import General.Neuron;
import General.PropertyReader;
import General.Singleton;
import General.SpecialCreator;
import General.Species;

public class Evolve {
	private static final double CLONE_CHANCE = .25;
	private static final double MAX_SPECIES_DELTA = Double.parseDouble(PropertyReader.getProperty("speciationThreshold"));
	private static final double DISJOINTWEIGHT = 1;
	private static final double EXCESSWEIGHT = 1;
	private static final double DIFWEIGHT = 1;
	public static Random rand = new Random();
	
	public static void runner(Singleton s) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, InstantiationException, ClassNotFoundException, InterruptedException {
		EvolveManager netManager = netManagerReflected(s);
		netManager.EvolveSetup();
		NeuralNetwork[] nns = s.getNetworks();
		long t1 = System.currentTimeMillis();
		// runs the networks for a minute to measure their performance
		if(Integer.parseInt(PropertyReader.getProperty("competing")) > 1){
			Competition.evolutionRunner((CompetitionSingleton) s);
		}
		else{
			while (System.currentTimeMillis()-t1 < Long.parseLong(PropertyReader.getProperty("timing"))){
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
	@SuppressWarnings("unchecked")
	private static EvolveManager netManagerReflected(Singleton s)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String type = PropertyReader.getProperty("type");
		Class<? extends EvolveManager> class1 = (Class<? extends EvolveManager>) Class.forName("BackEvolution."+ type +"."+ type+"NetManager");
		@SuppressWarnings("deprecation")
		EvolveManager netManager = class1.newInstance();
		return netManager;
	}
	public static NeuralNetwork[] evolve(NeuralNetwork[] nns,Singleton s) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
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
	private static void newNetworkAtLoc(Singleton s, NeuralNetwork[] originalPopulation, NeuralNetwork[] newnns, Double totalFitness, int loc) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
		InvocationTargetException, IOException, InstantiationException {
		//this random decides if the network will be cloned or bred.
		Double cloneVsCrossover=  Math.random();
		NeuralNetwork network;
		//cloned
		if (cloneVsCrossover <= CLONE_CHANCE){
			NeuralNetwork clonedParent = parentSelection(originalPopulation, totalFitness);
			network = clone(clonedParent, s);
		}
		//crossover
		else{
			NeuralNetwork parent1 = parentSelection(originalPopulation, totalFitness);
			NeuralNetwork parent2 = parentSelection(originalPopulation, totalFitness);
			network = crossover(parent1,parent2,s);
		}
		setSpecies(network, newnns, s);
		newnns[loc] = network;
	}
	private static void setSpecies(NeuralNetwork network, NeuralNetwork[] newnns,Singleton s) {
		boolean speciesFound = false;
		for(NeuralNetwork compared : newnns) {
			if(compared != null && !speciesFound){
				double delta = getSpeciesDelta(network, compared);
				if(delta < MAX_SPECIES_DELTA) {
					network.setSpecies(compared.getSpecies());
					speciesFound = true;
				}			
			}
		}
		if(!speciesFound) {
			network.setSpecies(new Species(network));
		}
	}
	public static double getSpeciesDelta(NeuralNetwork network, NeuralNetwork compare) {
		
		double weightdif=0;
		int totalmatches = 0;
		int numGenes;
		int excess = 0;
		int disjoint = 0;
		ArrayList<Gene> genes = geneArrayCreator(network);
		ArrayList<Gene> genes2 = geneArrayCreator(compare);
		if(genes2.size() > genes.size())numGenes =genes2.size();
		else numGenes = genes.size();
		if(numGenes < 20) numGenes = 1;
		for(Gene g: genes) {
			boolean matchfound = false;
			for(Gene g2: genes2) {
				if(!matchfound && g2.getID() == g.getID()) {
					matchfound = true;
					totalmatches++;
					weightdif= g.getWeight()-g2.getWeight();
				}				
			}
			if(!matchfound)excess++;
		}
		for(Gene g2: genes2) {
			boolean matchfound = false;
			for(Gene g: genes) {
				if(g2.getID() == g.getID()) {
					matchfound=true;
				}
			}
			if(!matchfound)disjoint++;
		}
		weightdif = weightdif/totalmatches;
		return (EXCESSWEIGHT*excess)/numGenes + (DISJOINTWEIGHT*disjoint)/numGenes + DIFWEIGHT * weightdif;
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
	private static NeuralNetwork clone(NeuralNetwork cloner, Singleton s) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, InstantiationException {
		//starts by creating the basic structure for the new network
		NeuralNetwork newnn = newNetwork(s);
		ArrayList<Layer> clonelayers = cloner.getLayers();
		//adds layer and neuron structure
		for (int i = 1; i < clonelayers.size()-1; i++){
				ArrayList<Neuron> ns = clonelayers.get(i).getNeurons();
				networkLayerCreation(newnn, ns.size(), s);					
		}
		layerTrackingReset(clonelayers);
		//adds genes to structure
		HashMap<Long, double[]> geneIdentities = getGeneIdentities(clonelayers,clonelayers.size());
		geneAdder(newnn, geneIdentities);
		//returns a mutated clone
		return mutate(newnn,s);
	}
	private static NeuralNetwork crossover(NeuralNetwork cross, NeuralNetwork over, Singleton s) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
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
			networkLayerCreation(newnn, numNeurons, s);
		}
		layerTrackingReset(lesslayers);
		layerTrackingReset(morelayers);
		HashMap<Long, double[]> geneIdentities = getGeneIdentities(morelayers, maxlayers);
		HashMap<Long, double[]> geneIdentities2 = getGeneIdentities(lesslayers, maxlayers);
		geneBreeder(geneIdentities, geneIdentities2);
		geneAdder(newnn, geneIdentities);
		geneAdder(newnn, geneIdentities2);
		return mutate(newnn,s);
	}
	private static void geneBreeder(HashMap<Long, double[]> geneIdentities, HashMap<Long, double[]> geneIdentities2) {
		for (long id : geneIdentities.keySet()){
			if(geneIdentities2.containsKey(id)) {
				double[] nums = geneIdentities.get(id);
				double weight1 = nums[4];
				geneIdentities.remove(id);
				double weight2 =  geneIdentities2.get(id)[4];
				geneIdentities2.remove(id);
				nums[4] = weight1/2+weight2/2;
				geneIdentities.put(id,nums);
				
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
		return (morenum > lessnum) ? morenum : lessnum;
	}
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static NeuralNetwork newNetwork(Singleton s) throws ClassNotFoundException, NoSuchMethodException,
	IllegalAccessException, InvocationTargetException, IOException, InstantiationException {
		String type = PropertyReader.getProperty("type");
		Class<? extends NeuralNetwork> networkClass = (Class<? extends NeuralNetwork>) Class.forName("BackEvolution."+type+"."+type+"Network");
		Layer[] puts = NetworkCreator.creator(s);
		Class<? extends SpecialCreator> managerClass = (Class<? extends SpecialCreator>) Class.forName("BackEvolution."+type+"."+type+"Creator");
		SpecialCreator manager = managerClass.newInstance();
		manager.InputOutputcreator(puts);
		Class<?>[] types2 = {Class.forName("BackEvolution.Layer"),Class.forName("BackEvolution.Layer")};
		Constructor<? extends NeuralNetwork> con2 = networkClass.getConstructor(types2);
		NeuralNetwork newnn = con2.newInstance(puts[0],puts[1]);
		return newnn;
	} 
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static void networkLayerCreation(NeuralNetwork newnn, int numNeurons, Singleton s)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		String type = PropertyReader.getProperty("type");
		Class<? extends Neuron> neuronClass = (Class<? extends Neuron>) Class.forName("BackEvolution."+type+"."+type+"Neuron");	
		Class<? extends Layer> layerClass = (Class<? extends Layer>) Class.forName("BackEvolution."+type+"."+type+"Layer");
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
	private static HashMap<Long, double[]> getGeneIdentities(ArrayList<Layer> layers, int maxlayers) {
		HashMap<Long, double[]> geneIdentities = new HashMap<Long, double[]>();
		for(int k =1; k <=layers.size(); k++){
			Layer l = layers.get(k-1);			
			for (Neuron n: l.getNeurons()){				
				for(Gene g : n.getGenes()){
					double data[] = new double[5];
					data[0] = k;
					data[1] = n.getNumber();	
					data[2] = g.getConnection().getNumber();
					data[3] = g.getConnection().getLayernumber();
					if(data[3] == layers.size() && data[3] < maxlayers)data[3]=maxlayers;
					data[4] = g.getWeight();
					long id = g.getID();
					geneIdentities.put(id, data);
				}
			}
		}
		return geneIdentities;
	}
	private static void geneAdder(NeuralNetwork newnn, HashMap<Long, double[]> geneIdentities) {
		for(long id : geneIdentities.keySet()){
			double[] nums = geneIdentities.get(id);
			Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getNeurons().get((int) nums[2]-1), nums[4], id);
			Neuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getNeurons().get((int) nums[1]-1);
			newNeuron.AddGenes(newGene);
			newGene.setInput(newNeuron);
		}
	}
	private static NeuralNetwork mutate(NeuralNetwork newnn, Singleton s) throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		NeuralNetManager.Neuraltracker(newnn);
		double mutationSelector = Math.random();
		ArrayList<Gene> genes = geneArrayCreator(newnn);
		if (mutationSelector < Double.parseDouble(PropertyReader.getProperty("removeProbability"))&& genes.size() > 0)geneMutation(mutationSelector, genes);
		else if (mutationSelector < Double.parseDouble(PropertyReader.getProperty("newGeneProbability")) || genes.size() == 0)newGeneInsert(newnn, s);
		else {
			if (newnn.getLayers().size() == 2 || mutationSelector > Double.parseDouble(PropertyReader.getProperty("existingLayerProbability")))newNeuronInNewLayer(newnn, genes, s);	
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
	private static void newNeuronInExistingLayer(NeuralNetwork newnn, Singleton s) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String type = PropertyReader.getProperty("type");
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
			Gene gene2 = new Gene(out, Math.random()*2-1, s);
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
			Gene gene1 = new Gene(n, Math.random()*2-1, s);
			in.AddGenes(gene1);
			gene1.setInput(in);
			Gene gene2 =new Gene(on,Math.random()*2-1, s);
			gene2.setInput(n);
			n.AddGenes(gene2);
			selected.addNeuron(n);
			n.setNumber(selected.getNeurons().size());
			n.setLayernumber(selected.getNumber());
		}
	}
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static void newNeuronInNewLayer(NeuralNetwork newnn, ArrayList<Gene> genes, Singleton s)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		//new neuron in new layer
		String type = PropertyReader.getProperty("type");
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
			n.AddGenes(new Gene(out, Math.random()*2 -1, s));
			gene.setInput(n);
		}
		else{
			ArrayList<Neuron> ns  = new ArrayList<Neuron>();
			for (Gene g : genes){
				ns.add(g.getConnection());
			}
			Gene gene = new Gene(n, Math.random()*2-1, s);
			n.addInput(gene);
			Gene gene2 = new Gene(outputlayer.getNeurons().get(0), Math.random()*2-1, s);
			if(outputlayer.getNeurons().size()-1 > 0) gene2 = new Gene(outputlayer.getNeurons().get(rand.nextInt(outputlayer.getNeurons().size()-1)), Math.random()*2-1, s);
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
	private static void newGeneInsert(NeuralNetwork newnn, Singleton s) {
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
		Gene g = new Gene(newnn.getLayers().get(layer2).getNeurons().get(neuron2), weight, s);
		newnn.getLayers().get(layer2).getNeurons().get(neuron2).addInput(g);
		for(int i = 0; i < in.getGenes().size();i++){
			if (newnn.getLayers().get(layer).getNeurons().get(neuron).getGenes().get(i).getConnection().getLayernumber() == layer2 &&newnn.getLayers().get(layer).getNeurons().get(neuron).getGenes().get(i).getConnection().getNumber() == neuron2){
				in.RemoveGenes(in.getGenes().get(i));
				i--;
			}
		}
		in.AddGenes(g);
	}
	private static void geneMutation(double selector, ArrayList<Gene> genes) {
		Gene gene= genes.get(0);
		if(genes.size() > 1) gene= genes.get(rand.nextInt(genes.size()-1));
		if(selector < Double.parseDouble(PropertyReader.getProperty("adjustProbability"))){
			//adjust weight
			gene.setWeight(gene.getWeight()*(1+(Math.random()*.1)));
		}
		else if (selector < Double.parseDouble(PropertyReader.getProperty("randomProbability"))){
			// new random weight
			gene.setWeight(Math.random()*2 - 1);
			
		}
		else{
			//disable/enable gene
			gene.remove();	
		}
	}
}
   