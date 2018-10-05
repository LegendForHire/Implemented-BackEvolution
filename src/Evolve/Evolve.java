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
import General.Gene;
import General.Layer;
import General.NetworkCreator;
import General.NeuralNetManager;
import General.NeuralNetwork;
import General.Neuron;
import General.PropertyReader;
import General.Singleton;
import General.SpecialCreator;
import General.SpecialNetManager;
import General.Species;

public class Evolve {
	private static final double CLONE_CHANCE = .25;
	private static final double MAX_SPECIES_DELTA = Double.parseDouble(PropertyReader.getProperty("speciationThreshold"));
	private static final double DISJOINTWEIGHT = 1;
	private static final double EXCESSWEIGHT = 1;
	private static final double DIFWEIGHT = 1;
	public static void runner(Singleton s) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, InstantiationException, ClassNotFoundException, InterruptedException {
		SpecialNetManager netManager = netManagerReflected(s);
		netManager.EvolveSetup();
		NeuralNetwork[] nns = s.getNetworks();
		long t1 = System.currentTimeMillis();
		// runs the networks for a minute to measure their performance
		if(Integer.parseInt(PropertyReader.getProperty("competing")) > 1){
			Competition.evolutionRunner(s);
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
	private static SpecialNetManager netManagerReflected(Singleton s)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String type = PropertyReader.getProperty("type");
		Class<? extends SpecialNetManager> class1 = (Class<? extends SpecialNetManager>) Class.forName("BackEvolution."+ type +"."+ type+"NetManager");
		@SuppressWarnings("deprecation")
		SpecialNetManager netManager = class1.newInstance();
		return netManager;
	}
	public static NeuralNetwork[] evolve(NeuralNetwork[] nns,Singleton s) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Arrays.sort(nns);
		NeuralNetwork[] halfnns = new NeuralNetwork[nns.length/2];
		//This will be the new population that is returned
		NeuralNetwork[] newnns = new NeuralNetwork[nns.length];
		// this keeps track of how many survivors were artifically selected for survival.	
		//adds in the rest of the survivors strictly based on merit
		Double totalFitness = fitnessSetup(nns, halfnns, newnns);
		//this is where new neural networks are born for the new population
		for (int i = nns.length/2; i<nns.length; i++){
			newNetworkAtLoc(s, halfnns, newnns, totalFitness, i);
		}
		
		return newnns;
		
	}
	private static void newNetworkAtLoc(Singleton s, NeuralNetwork[] originalPopulation, NeuralNetwork[] newnns, Double totalFitness, int loc) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
		InvocationTargetException, IOException, InstantiationException {
		//this random decides if the network will be cloned or bred.
		Double cloneVsCrossover=  Math.random();
		NeuralNetwork network;
		//cloned
		if (cloneVsCrossover <= CLONE_CHANCE){
			NeuralNetwork clonedParent = parentSelection(originalPopulation, totalFitness);
			network = Reproduce.clone(clonedParent, s);
		}
		//crossover
		else{
			NeuralNetwork parent1 = parentSelection(originalPopulation, totalFitness);
			NeuralNetwork parent2 = parentSelection(originalPopulation, totalFitness);
			network = Reproduce.crossover(parent1,parent2,s);
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
			Species newSpecies = new Species(network);
			network.setSpecies(newSpecies);
		}
	}
	public static double getSpeciesDelta(NeuralNetwork network, NeuralNetwork compare) {
		
		double weightdif=0;
		int totalmatches = 0;
		int numGenes;
		int excess = 0;
		int disjoint = 0;
		ArrayList<Gene> genes = Mutate.geneArrayCreator(network);
		ArrayList<Gene> genes2 = Mutate.geneArrayCreator(compare);
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
	private static Double fitnessSetup(NeuralNetwork[] nns, NeuralNetwork[] newnns, NeuralNetwork[] halfnns) {
		double totalFitness = 0.0;
		for (int i = 0; i < nns.length/2; i++){
			//makes sure duplicates aren't added.
			newnns[i] = nns[i];
			halfnns[i] = nns[i];
			totalFitness += nns[i].getFitness();
			
		}
		for(int i = nns.length/2; i < nns.length; i++) {
			//removes dead members from species
			nns[i].getSpecies().getNetworks().remove(nns[i]);
		}
		return totalFitness;
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
}
    