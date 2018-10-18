package Evolve;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import General.DataManager;
import General.Gene;
import General.Layer;
import General.MethodManager;
import General.NetworkCreator;
import General.NeuralNetwork;
import General.Neuron;
import General.PropertyReader;

public class Reproduce {
	
	public static NeuralNetwork clone(NeuralNetwork cloner, DataManager data) {
		NeuralNetwork newnn = newNetwork(data);
		ArrayList<Layer> clonelayers = cloner.getLayers();
		//adds layer and neuron structure
		for (int i = 1; i < clonelayers.size()-1; i++){
				ArrayList<Neuron> ns = clonelayers.get(i).getNeurons();
				networkLayerCreation(newnn, ns.size());					
		}
		layerTrackingReset(clonelayers);
		//adds genes to structure
		HashMap<Long, double[]> geneIdentities = getGeneIdentities(clonelayers,clonelayers.size());
		geneAdder(newnn, geneIdentities);
		//returns a mutated clone
		return Mutate.mutate(newnn,data);
	}
	public static NeuralNetwork crossover(NeuralNetwork cross, NeuralNetwork over, DataManager data) {
		NeuralNetwork newnn = newNetwork(data);
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
			networkLayerCreation(newnn, numNeurons);
		}
		layerTrackingReset(lesslayers);
		layerTrackingReset(morelayers);
		HashMap<Long, double[]> geneIdentities = getGeneIdentities(morelayers, maxlayers);
		HashMap<Long, double[]> geneIdentities2 = getGeneIdentities(lesslayers, maxlayers);
		geneBreeder(geneIdentities, geneIdentities2);
		geneAdder(newnn, geneIdentities);
		geneAdder(newnn, geneIdentities2);
		return Mutate.mutate(newnn, data);
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
	private static NeuralNetwork newNetwork(DataManager data) {
		String type = PropertyReader.getProperty("type");
		
		try {
			Class<? extends NeuralNetwork> networkClass = (Class<? extends NeuralNetwork>) Class.forName("BackEvolution."+type+"."+type+"Network");
			Layer[] puts = NetworkCreator.creator();
			MethodManager manager = data.getMethods();
			manager.InputOutputcreator(puts, data);
			Class<?>[] types2 = {Layer.class,Layer.class,DataManager.class};
			Constructor<? extends NeuralNetwork> con2 = networkClass.getConstructor(types2);
			NeuralNetwork newnn = con2.newInstance(puts[0],puts[1], data);
			return newnn;
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Thread.currentThread().stop();
		}
		return null;
		
	} 
	@SuppressWarnings({ "unchecked"})
	private static void networkLayerCreation(NeuralNetwork newnn, int numNeurons) {
		String type = PropertyReader.getProperty("type");
		
		try {
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
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
}
