import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Evolve {
	public static Random rand = new Random();
	public static Singleton s = Singleton.getInstance();
	public static NeuralNetwork[] evolve(NeuralNetwork[] nns,Market[] markets) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//double checks to makesure the arrays arestill properly sorted with the most recent market data.
		Arrays.sort(nns);
		//Half of the neuralnetworks will survive.
		NeuralNetwork[] halfnns = new NeuralNetwork[nns.length/2];
		int maxLayers = 2;
		//This will be the new population that is returned
		NeuralNetwork[] newnns = new NeuralNetwork[nns.length];
		// this keeps track of how many survivors were artifically selected for survival.
		int tracker = 0;
		//finds the most layers among all the neural networks
		for(NeuralNetwork nn : nns){
			nn.setAge(nn.getAge()+1);
			ArrayList<Layer> l = nn.getLayers();
			if (l.size() > maxLayers){
				maxLayers = l.size();
			}
		}
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
		//adds in the rest of the survivors strictly based on merit
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
		//this is where new neural networks are born for the new population
		for (int i = nns.length/2; i<nns.length; i++){
			//this random decides if the network will be cloned or bred.
			Double cloneVsCrossover=  Math.random();
			//cloned
			if (cloneVsCrossover <= .25){
				//this decides which network it will be a clone of
				Double who = Math.random()*totalFitness;
				NeuralNetwork cloner = null;
				boolean selected = false;
				for (NeuralNetwork nn : halfnns){
					who = who -nn.getFitness();
					if (who <= 0 && !selected){
						cloner = nn;
						selected = true;
					}
				}
				if (cloner == null){
					cloner = halfnns[99];
				}
				//cloning
				newnns[i] = clone(cloner);
			}
			//bred
			else{
				//decides parent 1
				Double who = Math.random()*totalFitness;
				NeuralNetwork cross = null;
				boolean selected = false;
				for (int k = 0; k <halfnns.length;k++){
					who = who -halfnns[k].getFitness();
					if (who <= 0 && !selected){
						cross = halfnns[k];
						selected = true;
					}
				}
				if(cross == null){
					cross = halfnns[99];
				}
				ArrayList<NeuralNetwork> equalLayers = new ArrayList<NeuralNetwork>();
				//creates an array of neural networks with the same number of layers as the parent otherwise breeding is impossible
				double equalfitness = 0;
				for(NeuralNetwork nn:halfnns){
					if (nn.getLayers().size() == cross.getLayers().size()){
						equalLayers.add(nn);
						equalfitness += nn.getFitness();
					}
				}
				//selects from the previously created array
				if(equalfitness > 0){
				who = Math.random()*equalfitness;
				NeuralNetwork over = null;
				selected = false;
				for (int k = 0; k <equalLayers.size();k++){
					who = who -equalLayers.get(k).getFitness();
					if (who <= 0 && !selected){
						over = equalLayers.get(k);
						selected = true;
					}
				}
				if (over == null){
					over = halfnns[0];
				}
				//breeds
				newnns[i] = crossover(cross,over);
				}
				else{
					newnns[i] = clone(cross);
				}
			}
		}
		
		return newnns;
		
	}
	private static NeuralNetwork clone(NeuralNetwork cloner) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		//starts by creating the basic structure for te new network
		Layer[] puts = NetworkCreator.creator(s.getMarkets());
		ArrayList<Layer> clonelayers = cloner.getLayers();
		NeuralNetwork newnn = new NeuralNetwork(puts[0],puts[1], s.getMarkets());
		//adds layer and neuron structure
		for (int i1 = 1; i1 < clonelayers.size()-1; i1++){
			if (!clonelayers.get(i1).isInput()&&!clonelayers.get(i1).isOutput()){
				ArrayList<Neuron> ns = clonelayers.get(i1).getNeurons();
				Layer newl = new Layer(false,false);
				newl.setNumber(i1);
				for (int j = 0; j < ns.size(); j++) {
					Neuron newn = new Neuron();
					newl.addNeuron(newn);
					newn.setNumber(ns.size());
					newn.setLayernumber(newl.getNumber());
				}
				newnn.addLayer(newl);
				
			}	
		}
		for (Layer l : clonelayers){
			if (l.isInput()){
			for (InputNeuron n : l.getINeurons()){
				n.setLayernumber(l.getNumber());
			}
		}
		else if (l.isOutput()){
			for (OutputNeuron n : l.getONeurons()){
				n.setLayernumber(l.getNumber());
			}
			
		}
		else{
			for (Neuron n : l.getNeurons()){
				n.setLayernumber(l.getNumber());
			}
		}
		
		}
		//adds genes to struture
		ArrayList<double[]> geneIdentities = new ArrayList<double[]>();
		for(int k =1; k <=clonelayers.size(); k++){
			Layer l = clonelayers.get(k-1);
			if (l.isInput()){
				for (InputNeuron n: l.getINeurons()){				
					for(Gene g : n.getGenes()){
						double data[] = new double[5];
						data[0] = l.getNumber();
						data[1] = n.getNumber();	
						data[2] = g.getConnection().getNumber();
						data[3] = g.getConnection().getLayernumber();
						data[4] = g.getWeight();
						geneIdentities.add(data);
					}
				}
			}
			else{
				for (Neuron n: l.getNeurons()){				
					for(Gene g : n.getGenes()){
						double data[] = new double[5];
						data[0] = l.getNumber();
						data[1] = n.getNumber();	
						data[2] = g.getConnection().getNumber();
						data[3] = g.getConnection().getLayernumber();
						data[4] = g.getWeight();
						geneIdentities.add(data);
					}
				}
			}
		}
		for(double[] nums : geneIdentities){
			if(nums[0] == 1){
				if (newnn.getLayers().get((int) nums[3]-1).isOutput()){
					Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getONeurons().get((int) nums[2]-1), nums[4]);
					InputNeuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getINeurons().get((int) nums[1]-1);
					newNeuron.AddGenes(newGene);
					newGene.setInputI(newNeuron);
				}
				else {
					Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getNeurons().get((int) nums[2]-1), nums[4]);
					InputNeuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getINeurons().get((int) nums[1]-1);
					newNeuron.AddGenes(newGene);
					newGene.setInputI(newNeuron);
				}
			}
			else{
				if (newnn.getLayers().get((int) nums[3]-1).isOutput()){
					Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getONeurons().get((int) nums[2]-1), nums[4]);
					Neuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getNeurons().get((int) nums[1]-1);
					newNeuron.AddGenes(newGene);
					newGene.setInput(newNeuron);
				}
				else { 
					Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getNeurons().get((int) nums[2]-1), nums[4]);
					Neuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getNeurons().get((int) nums[1]-1);
					newNeuron.AddGenes(newGene);
					newGene.setInput(newNeuron);
				}
			}
		}
		//returns a mutated clone
		return mutate(newnn);
	}
	//where breeding takes place
	private static NeuralNetwork crossover(NeuralNetwork cross, NeuralNetwork over) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//determines which of the two networks was more fit and less fit
		NeuralNetwork newnn = null;
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
		//creates the structure for the new network
		Layer[] puts = NetworkCreator.creator(s.getMarkets());
		newnn = new NeuralNetwork(puts[0],puts[1],s.getMarkets());
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
			Layer newl = new Layer(false,false);
			newnn.addLayer(newl);
			newl.setNumber(newnn.getLayers().size()-1);
			for (int j = 0; j < morenum || j < lessnum; j++){
				Neuron newn = new Neuron();
				newl.addNeuron(newn);
				newn.setLayernumber(newl.getNumber());
				newn.setNumber(newl.getNeurons().size()-1);
			}
		}
		for (int i = 1; i <=lesslayers.size(); i++){
			Layer l = lesslayers.get(i-1);
			if (l.isInput()){
			for (InputNeuron n : l.getINeurons()){
				n.setLayernumber(i);
			}
		}
		else if (l.isOutput()){
			for (OutputNeuron n : l.getONeurons()){
				n.setLayernumber(i);
			}
			
		}
		else{
			for (Neuron n : l.getNeurons()){
				n.setLayernumber(i);
			}
		}
		
		}
		for (int i = 1; i <=morelayers.size(); i++){
			Layer l = morelayers.get(i-1);
			if (l.isInput()){
			for (InputNeuron n : l.getINeurons()){
				n.setLayernumber(i);
			}
		}
		else if (l.isOutput()){
			for (OutputNeuron n : l.getONeurons()){
				n.setLayernumber(i);
			}
			
		}
		else{
			for (Neuron n : l.getNeurons()){
				n.setLayernumber(i);
			}
		}
		
		}
		//adds genes to the structure. if the same conection exists in the more fit network, it gets preference over the less fit network. if one or the other does not have this gene at all it gets added. currently more fit is always selected may add random chance that the less fit networks gene is selected.
		//gets all the data from the more fit layer.
		ArrayList<double[]> geneIdentities = new ArrayList<double[]>();
		for(Layer l : morelayers){
			if (l.isInput()){
				for (InputNeuron n: l.getINeurons()){
					for(Gene g : n.getGenes()){
						double data[] = new double[5];
						data[0] = l.getNumber();
						data[1] = n.getNumber();
						data[2] = g.getConnection().getNumber();
						if (morelayers.get(g.getConnection().getLayernumber()-1).isOutput()) data[3] = maxlayers;
						else data[3] = g.getConnection().getLayernumber();
						data[4] = g.getWeight();
						geneIdentities.add(data);
						
					}
				}
			}
			else{
				for (Neuron n : l.getNeurons()){
					for(Gene g : n.getGenes()){
						double data[] = new double[5];
						data[0] = l.getNumber();
						data[1] = n.getNumber();
						data[2] = g.getConnection().getNumber();
						if (morelayers.get(g.getConnection().getLayernumber()-1).isOutput()) data[3] = maxlayers;
						else data[3] = g.getConnection().getLayernumber();
						data[4] = g.getWeight();
						geneIdentities.add(data);
						
					}
				}
			}
			
			
		}
		//gets  all the gene data from the less fit layer.
		ArrayList<double[]> geneIdentities2 = new ArrayList<double[]>();
		for(Layer l : lesslayers){
			if (l.isInput()){
				for (InputNeuron n: l.getINeurons()){
					for(Gene g : n.getGenes()){						
						double data[] = new double[5];
						data[1] = n.getNumber();
						data[0] = l.getNumber();
						data[2] = g.getConnection().getNumber();
						if (lesslayers.get(g.getConnection().getLayernumber()-1).isOutput()) data[3] = maxlayers;
						else data[3] = g.getConnection().getLayernumber();
						data[4] = g.getWeight();
						geneIdentities2.add(data);
						
					}
				}
			}
			else{
				for (Neuron n : l.getNeurons()){
					for(Gene g : n.getGenes()){
						double data[] = new double[5];
						data[0] = l.getNumber();
						data[1] = n.getNumber();
						data[2] = g.getConnection().getNumber();
						if (lesslayers.get(g.getConnection().getLayernumber()-1).isOutput()) data[3] = maxlayers;
						else data[3] = g.getConnection().getLayernumber();
						data[4] = g.getWeight();
						geneIdentities2.add(data);
					}
				}
			}
			
			
		}
		//if two genes connect the same neuron, it averages the weights of them and removes one leaving the other with the average weight
		for (double[] nums : geneIdentities){
			for (int i = 0; i < geneIdentities2.size(); i++){
				double[] nums2 = geneIdentities2.get(i);
				if (nums[0] == nums2[0] && nums[1] == nums2[1] && nums[2] == nums2[2] && nums[3] == nums2[3] && nums[4] == nums2[4]){
					nums[4] = (nums[4] + nums2[4])/2;
					geneIdentities2.remove(nums2);
					i--;
				}
				else if (nums[0] == nums2[0] && nums[1] == nums2[1] && nums[2] == nums2[2] && nums[3] == nums2[3]){
					geneIdentities2.remove(nums2);
					i--;
				}
			}
		}
		//adds the new genes to the new neural network based on the gene data.
		for(double[] nums : geneIdentities){
			if(nums[0] == 1){
				if (newnn.getLayers().get((int) nums[3]-1).isOutput()){
					Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getONeurons().get((int) nums[2]-1), nums[4]);
					InputNeuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getINeurons().get((int) nums[1]-1);
					newNeuron.AddGenes(newGene);
					newGene.setInputI(newNeuron);
				}
				else {
					Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getNeurons().get((int) nums[2]-1), nums[4]);
					InputNeuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getINeurons().get((int) nums[1]-1);
					newNeuron.AddGenes(newGene);
					newGene.setInputI(newNeuron);
				}
			}
			else{
				if (newnn.getLayers().get((int) nums[3]-1).isOutput()){
					Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getONeurons().get((int) nums[2]-1), nums[4]);
					Neuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getNeurons().get((int) nums[1]-1);
					newNeuron.AddGenes(newGene);
					newGene.setInput(newNeuron);
				}
				else { 
					Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getNeurons().get((int) nums[2]-1), nums[4]);
					Neuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getNeurons().get((int) nums[1]-1);
					newNeuron.AddGenes(newGene);
					newGene.setInput(newNeuron);
				}
			}
		}
		for(double[] nums : geneIdentities2){
			if(nums[0] == 1){
				if (newnn.getLayers().get((int) nums[3]-1).isOutput()){
					Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getONeurons().get((int) nums[2]-1), nums[4]);
					InputNeuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getINeurons().get((int) nums[1]-1);
					newNeuron.AddGenes(newGene);
					newGene.setInputI(newNeuron);
				}
				else {
					Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getNeurons().get((int) nums[2]-1), nums[4]);
					InputNeuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getINeurons().get((int) nums[1]-1);
					newNeuron.AddGenes(newGene);
					newGene.setInputI(newNeuron);
				}
			}
			else{
				if (newnn.getLayers().get((int) nums[3]-1).isOutput()){
					Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getONeurons().get((int) nums[2]-1), nums[4]);
					Neuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getNeurons().get((int) nums[1]-1);
					newNeuron.AddGenes(newGene);
					newGene.setInput(newNeuron);
				}
				else { 
					Gene newGene = new Gene(newnn.getLayers().get((int) nums[3]-1).getNeurons().get((int) nums[2]-1), nums[4]);
					Neuron newNeuron = newnn.getLayers().get((int) (nums[0]-1)).getNeurons().get((int) nums[1]-1);
					newNeuron.AddGenes(newGene);
					newGene.setInput(newNeuron);
				}
			}
		}
		//returns mutated bred network
		return mutate(newnn);
	}
 	//where mutating takes place
	private static NeuralNetwork mutate(NeuralNetwork newnn) {
		NeuralNetManager.Neuraltracker(newnn);
		//determines type of mutation
		double selector = Math.random();
		//creates an arraylist of all genes
		ArrayList<Gene> genes = new ArrayList<Gene>();
		for (Layer l : newnn.getLayers()){
			if (l.isInput()){
				for (InputNeuron n : l.getINeurons()){
					for (Gene g : n.getGenes())
						genes.add(g);
				}
			}
			else{
				for (Neuron n : l.getNeurons()){						
					for (Gene g : n.getGenes())genes.add(g);
						
					
				}
			}
		}
		// gene mutation
		if (selector < .8 && genes.size() > 0){
			
			
			Gene gene= genes.get(0);
			if(genes.size() > 1) gene= genes.get(rand.nextInt(genes.size()-1));
			if(selector < .65) {
				//adjust weight
				gene.setWeight(gene.getWeight()*(1+(Math.random()*.1)));
			}
			else if (selector < .75){
				// new random weight
				gene.setWeight(Math.random()*2 - 1);
				
			}
			else{
				//disable/enable gene
				gene.toggle();	
			}
		
		}
		else if (selector <.97 || genes.size() == 0){
			// new gene
			int layer = 0;
			if (newnn.getLayers().size() > 2) layer = rand.nextInt(newnn.getLayers().size()-2);
			int neuron = 0;
			if (newnn.getLayers().get(layer).isInput()){
				if (newnn.getLayers().get(layer).getINeurons().size() > 1){
					neuron = rand.nextInt(newnn.getLayers().get(layer).getINeurons().size()-1);
				}
			}
			else{
				if (newnn.getLayers().get(layer).getNeurons().size() > 1){
					neuron = rand.nextInt(newnn.getLayers().get(layer).getNeurons().size()-1);
				}
			}
			int layer2 = newnn.getLayers().size()-1;
			if (newnn.getLayers().size()-2-layer > 0) layer2 = layer + 1 + rand.nextInt(newnn.getLayers().size()-2-layer);
			int neuron2 = 0;
			if (newnn.getLayers().get(layer2).isOutput()){
				if(newnn.getLayers().get(layer2).getONeurons().size()>1){
					neuron2 = rand.nextInt(newnn.getLayers().get(layer2).getONeurons().size()-1);
				}
			}
			else{
				if (newnn.getLayers().get(layer2).getNeurons().size() > 1){
					neuron2 =  rand.nextInt(newnn.getLayers().get(layer2).getNeurons().size()-1);
				}
			}
 			double weight = Math.random()*2 - 1;
 			Neuron in = null;
 			if (layer == 0)in = newnn.getLayers().get(layer).getINeurons().get(neuron);
 			else in = newnn.getLayers().get(layer).getNeurons().get(neuron);
 			Gene g = null;
 			if (layer2 == newnn.getLayers().size()-1){
 				g = new Gene(newnn.getLayers().get(layer2).getONeurons().get(neuron2), weight);
 				newnn.getLayers().get(layer2).getONeurons().get(neuron2).addInput(g);
 				
 			}
 			else{
 				g = new Gene(newnn.getLayers().get(layer2).getNeurons().get(neuron2), weight);
 				newnn.getLayers().get(layer2).getNeurons().get(neuron2).addInput(g);
 			}
 			g.setInput(in);
 			if (newnn.getLayers().get(layer).isInput()){
 				if (newnn.getLayers().get(layer2).isOutput()){
 					for(int i = 0; i < in.getGenes().size();i++){
 	 					if (in.getGenes().get(i).getOConnection().getLayernumber() == layer2 &&in.getGenes().get(i).getOConnection().getNumber() == neuron2){
 	 						in.RemoveGenes(in.getGenes().get(i));
 	 						i--;
 	 					}
 	 				}
 					in.AddGenes(g);
 				}
 				else{
 					for(int i = 0; i < in.getGenes().size();i++){
 						if (layer == 0) {
 							if (newnn.getLayers().get(0).getINeurons().get(neuron).getGenes().get(i).getConnection().getLayernumber() == layer2 &&newnn.getLayers().get(0).getINeurons().get(neuron).getGenes().get(i).getConnection().getNumber() == neuron2){
 	 	 						in.RemoveGenes(in.getGenes().get(i));
 	 	 						i--;
 	 	 					}
 						}
 						else if (newnn.getLayers().get(layer).getNeurons().get(neuron).getGenes().get(i).getConnection().getLayernumber() == layer2 &&newnn.getLayers().get(layer).getNeurons().get(neuron).getGenes().get(i).getConnection().getNumber() == neuron2){
 	 						in.RemoveGenes(in.getGenes().get(i));
 	 						i--;
 	 					}
 	 				}
 					in.AddGenes(g);
 				}
 			}
 			else{
 				if (newnn.getLayers().get(layer2).isOutput()){
 					for(int i = 0; i < in.getGenes().size();i++){
 	 					if (in.getGenes().get(i).getOConnection().getLayernumber() == layer2 &&in.getGenes().get(i).getOConnection().getNumber() == neuron2){
 	 						in.RemoveGenes(in.getGenes().get(i));
 	 						i--;
 	 					}
 	 				}
 					in.AddGenes(g);
 				}
 				else{
 					for(int i = 0; i < in.getGenes().size();i++){
 	 					if (newnn.getLayers().get(layer).getNeurons().get(neuron).getGenes().get(i).getConnection().getLayernumber() == layer2 &&newnn.getLayers().get(layer).getNeurons().get(neuron).getGenes().get(i).getConnection().getNumber() == neuron2){
 	 						in.RemoveGenes(in.getGenes().get(i));
 	 						i--;
 	 					}
 	 				}
 					in.AddGenes(g);
 				}
 			}
		}
		else{
			//new neuron
			if (newnn.getLayers().size() == 2 || selector > .999 + ((newnn.getLayers().size()-2)*.00004)){
				//new neuron in new layer
				Layer l = new Layer(false, false);
				newnn.addLayer(l);
				l.setNumber(newnn.getLayers().size()-1);
				Neuron n = new Neuron();
				l.addNeuron(n);
				n.setNumber(1);
				n.setLayernumber(newnn.getLayers().size()-1);
				Layer outputlayer = newnn.getLayers().get(newnn.getLayers().size()-1);
				outputlayer.setNumber(newnn.getLayers().size());
				for (OutputNeuron on : outputlayer.getONeurons()){
					on.setLayernumber(newnn.getLayers().size());
				}
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
					Gene gene2 = new Gene(outputlayer.getONeurons().get(0), Math.random()*2-1);
					if(outputlayer.getONeurons().size()-1 > 0) gene2 = new Gene(outputlayer.getONeurons().get(rand.nextInt(outputlayer.getONeurons().size()-1)), Math.random()*2-1);
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
			else{
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
					if (l.isInput()){
						for (InputNeuron n : l.getINeurons()){
							for(Gene g : n.getGenes()){
								if (g.getConnection().getLayernumber() > selected.getNumber() && n.getLayernumber() < selected.getNumber())genes2.add(g);
							}
						}
					}
					else{
						for (Neuron n : l.getNeurons()){
							for(Gene g : n.getGenes()){
								if (g.getConnection().getLayernumber() > selected.getNumber() && n.getLayernumber() < selected.getNumber())genes2.add(g);
							}
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
					Neuron n = new Neuron();
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
					ArrayList<InputNeuron> ns = newnn.getLayers().get(0).getINeurons();
					InputNeuron in = null;
					try{
						in = ns.get(rand.nextInt(ns.size()-1));
					}
					catch(Exception f){
						in = ns.get(0);
					}
					ArrayList<OutputNeuron> ns2 = newnn.getLayers().get(newnn.getLayers().size()-1).getONeurons();
					OutputNeuron on = null;
					try{
						on = ns2.get(rand.nextInt(ns2.size()-1));
					}
					catch(Exception f){
						on = ns2.get(0);
					}
					Neuron n = new Neuron();
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
		}
		
		return newnn;
	}


}
