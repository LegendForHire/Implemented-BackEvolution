package BackEvolution.Tester;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Evolve.Mutate;
import NeuralNetwork.Gene;
import NeuralNetwork.Layer;

class MutateTest {

	private TestDataManager td;
	private TestNetwork tn;
	private TestNeuron input;
	private TestNeuron output1;
	private TestNeuron output2;
	private Gene gene;
	private TestLayer inputL;
	private TestLayer outputL;
	private Mutate mutate;

	@BeforeEach
	void setUp() throws Exception {
		td = new TestDataManager();
		input = new TestNeuron();
		output1 = new TestNeuron();
		output2 = new TestNeuron();
		gene = new Gene(output1, 1, td);
		inputL = new TestLayer(true, false);
		outputL = new TestLayer(false, true);
		inputL.addNeuron(input);
		outputL.addNeuron(output1);
		outputL.addNeuron(output2);
		input.AddGenes(gene);
		tn = new TestNetwork(inputL, outputL, td);	
		mutate = new Mutate(td);
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	@Test
	void testGeneArray() {
		ArrayList<Gene> genes = mutate.geneArrayCreator(tn);
		assert(genes.contains(gene));
		assert(genes.size() == 1);	
		gene.remove();
		genes = mutate.geneArrayCreator(tn);
		assert(!genes.contains(gene));
		assert(genes.size() == 0);
		Gene gene2 = new Gene(output2, 0, td);
		input.AddGenes(gene2);
		input.AddGenes(gene);
		genes = mutate.geneArrayCreator(tn);
		assert(genes.contains(gene));
		assert(genes.contains(gene2));
		assert(genes.size() == 2);
		
	}
	@Test
	void testGeneAdjust() {
		double original = gene.getWeight();
		mutate.geneAdjust(gene);
		assert(gene.getWeight() >= original*.9 && gene.getWeight() <= original*1.1);
	}
	@Test
	void testGeneDisable() {
		mutate.geneDisable(gene);
		assert(!input.getGenes().contains(gene));
	}
	@Test
	void testRandomWeight() {
		mutate.geneAdjust(gene);
		assert(gene.getWeight() >= -1 && gene.getWeight() <= 1);
	}
	@Test
	void testNewNinNewL() {
		ArrayList<Gene> genes = mutate.geneArrayCreator(tn);
		mutate.newNeuronInNewLayer(tn, genes);
	    genes = mutate.geneArrayCreator(tn);
		assert(genes.size() == 2);
		assert(gene.getConnection() != output1);
		assert(gene.getConnection().getGenes().get(0).getConnection() == output1);
	}
	@Test
	void testNewNinExistingL() {
		ArrayList<Gene> genes = mutate.geneArrayCreator(tn);
		Layer l = new TestLayer(false,false);
		tn.addLayer(l);
		mutate.newNeuronInExistingLayer(tn);
	    genes = mutate.geneArrayCreator(tn);
		assert(genes.size() == 2);
		assert(gene.getConnection() != output1);
		assert(gene.getConnection().getGenes().get(0).getConnection() == output1);
		assert(tn.getLayers().get(gene.getConnection().getLayernumber()-1) == l); 
	}

}
