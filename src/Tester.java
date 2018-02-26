
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.*;

/**/
@SuppressWarnings("unused")

public class Tester {
	static Random rand = new Random();
	public static final double LEARNING_RATE = .01;
	public static final double MOMENTUM = .25;
	public static final double ALLOWABLE_ERROR= 250;
	public static double totalGlobalError;
	/*created to keep track of the main neural network's wallets which I use to see if the program has become profitable*/
	static ArrayList<Wallet> wallets;
	/*This is a variable created to see what the value of the wallets would be if no action was taken used to see profitability in output files*/
	private static ArrayList<Wallet> noactwallets;
	private static Session session;
	private static ChannelSftp sftpChannel;
	/* main methods initializes all the separate threads threads*/
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, SftpException, JSchException{
		/* This thread creates a thread for each market to keep it updated with the latest data*/
		try {
		JSch jsch = new JSch();
		session = jsch.getSession("LegendForHire", "71.71.87.235");
		session.setPassword("tFA45w&5f");
		session.setConfig("StrictHostKeyChecking", "no");
        System.out.println("Establishing Connection...");
        session.connect();
            System.out.println("Connection established.");
        System.out.println("Crating SFTP Channel.");
        sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();
		}
		catch (Exception e){
			
		}
        Thread thread4 = new Thread(){
        	public void run(){
        		while(true){
        		System.out.println("Type 'q' to quit");
        		@SuppressWarnings("resource")
			Scanner in = new Scanner(System.in);
        		String quit = in.nextLine();
        		if(quit.equals("q")) {
        			System.exit(1);
        		}
        		}
        	}
        };
        thread4.start();
        Thread thread5 = new Thread(){
        	public void run(){
        		while(true){       			
        			try {
        			long t1 = System.currentTimeMillis();
        			while(System.currentTimeMillis()-t1 <14400000);
        			sftpChannel.disconnect();
        			session.disconnect();
        			JSch jsch = new JSch();
        			session = jsch.getSession("LegendForHire", "71.71.87.235");
        			session.setPassword("tFA45w&5f");
        			session.setConfig("StrictHostKeyChecking", "no");
        	        session.connect();
        	        sftpChannel = (ChannelSftp) session.openChannel("sftp");
        	        sftpChannel.connect();
        	        
        			} 
        			catch (JSchException e) {
				}
        		}
        	}
        };
        thread5.start();
		long t1 = System.currentTimeMillis();
		Market[] markets = marketCreator();
		Thread thread1 = new Thread() {
			public void run(){
				try {
					updater(markets);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		thread1.start();
		/* had to put a wait time here for the threads from the previous thread to get started*/
		long t = System.currentTimeMillis();
		while(System.currentTimeMillis() - t < 20000);
		NeuralNetwork[] nns = CreateNetworks(200, markets); 
		//This thread is the actual genetic algorithm where the neural networks evolve.
		Thread thread2 = new Thread() {
			public void run(){
				try {
					RunNetworks(nns, markets);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException | ClassNotFoundException | NoSuchMethodException | SecurityException | SftpException | InterruptedException e ) {
					File eFile = new File("AIError"+System.currentTimeMillis());
					try {
						PrintWriter eWriter = new PrintWriter(eFile);
						e.printStackTrace(eWriter);
						eWriter.close();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		};		
		thread2.start();
		long t2 = System.currentTimeMillis();
		System.out.println(t2-t1);
		wallets = new ArrayList<Wallet>();
		noactwallets = new ArrayList<Wallet>();
		URL currencies = new URL("https://bittrex.com/api/v1.1/public/getCurrencies");
		BufferedReader in = new BufferedReader(new InputStreamReader(currencies.openStream()));
		String[] currencyList = in.readLine().substring(39).split("},");
		for (String c : currencyList){
			c = c.substring(13);
			c = c.replace(c.substring(c.indexOf("\"")), "");
			wallets.add(new Wallet(c, 50));
			noactwallets.add(new Wallet(c, 50));
		}
		// this thread is always running with the current most fit neural network and outputs a profits file that lets em see if the neural networks are profitable yet.
		Thread thread3 = new Thread(){
			public void run(){
				try {
					main(markets);
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException | IOException | SftpException e) {
					File eFile = new File("ProfitError"+System.currentTimeMillis());
					try {
						PrintWriter eWriter = new PrintWriter(eFile);
						e.printStackTrace(eWriter);
						eWriter.close();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		};
		t1 = System.currentTimeMillis();
		while(System.currentTimeMillis()-t1 < 86400000);
		thread3.start();
		Thread thread6 = new Thread() {
			@SuppressWarnings("deprecation")
			public void run() {
				while(true) {
				try {
					thread2.join();
					thread2.start();
					thread3.stop();
					thread3.start();
					thread4.stop();
					thread4.start();
					thread5.stop();
					thread5.start();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
		};
		thread6.start();
	}
	// This is the method where the nEuralNetworks learn.
	private static void RunNetworks(NeuralNetwork[] nns, Market[] markets) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, InterruptedException, SftpException {		
			int i = 1;
			
			while (true){
				for (NeuralNetwork nn : nns){
					nn.restartWallets();
				}
			totalGlobalError = ALLOWABLE_ERROR/(Math.log(i)*3+1) +1;
			System.out.println("Iteration" + i);
			// see method description
			Neuraltracker(nns);	
			// this is where the back propagation learning step for the neural networks run. currently I have them set to run for one minute before evaluating
			while(totalGlobalError > ALLOWABLE_ERROR/(Math.log(i)*3+1)){
				//set old values for back propagation step
				for(Market m: markets){
					m.setOld();
				}
				for (NeuralNetwork nn : nns){
					RunNetwork(nn);					
				}
				long t1 = System.currentTimeMillis();
				while(System.currentTimeMillis() - t1 < 60000);
				backpropagate(nns);		
				System.out.println("Total Global Error:" + totalGlobalError); 
			}
			System.out.println("backpropagation complete");
			//Just so it's easy to keep track of how well things are doing all of the wallets are restarted to a default state at the beginning of each run
			for (NeuralNetwork nn : nns){
				nn.restartWallets();
			}
			System.out.println("Wallets Restarted");
			long t1 = System.currentTimeMillis();
			while (System.currentTimeMillis()-t1 < 60000){
				for (NeuralNetwork nn : nns){
					RunNetwork(nn);					
				}	
			}
			//Updates the fitness for each neural network 
			for (NeuralNetwork nn : nns){
				nn.updateFitness();				
			}
			System.out.println("Fitness Determined");
			// determines the fitness if no action was taken
			double noact = 0;
			for (Wallet w : noactwallets){
				double amt = w.getAmmount();
				if (w.getName().equals("BTC")){
					noact+= amt;
				}
				else if (!w.getName().equals("XBB")&&!w.getName().equals("HKG")){
					for (Market market : markets){
						if (market.getMarketName().equals("BTC-" + w.getName())){
							noact += amt*market.getData(3);
						}
					
					}
				}
			}
			// sorts the neural networks from most fit to least fit.
			Arrays.sort(nns);
			//see method description
			Neuraltracker(nns);
			// saves the current state of the neural networks.
			save(nns, markets, noact);
			System.out.println("Last state saved");
			//evolution method
			nns = evolve(nns, markets);
			System.out.println("Iteration " + (i++) + " Complete");
			}
			
		
	}
	//saves the state of the neural networks.
	private static void save(NeuralNetwork[] nns,Market[] markets,double noact) throws IOException, SftpException {
		long t = System.currentTimeMillis();
		File out;
		File recent;
		PrintWriter fout;
		PrintWriter frecent;
//		try {
			out = new File("Generation.txt");
			recent = new File("MostRecent.txt");
			fout = new PrintWriter(out);
			frecent = new PrintWriter(recent);
			frecent.println(t);
			frecent.close();
			try{
				sftpChannel.put("MostRecent.txt","MostRecent.txt");
			}
			catch(Exception e){
		
			}
			
//		}
//		catch (Exception e) {
//			try {	
//				out = new File("H:/NeuralNetworks/Generation" + t + ".txt");
//				recent = new File("H:/NeuralNetworks/MostRecent.txt");
//				fout = new PrintWriter(out);
//				frecent = new PrintWriter(recent);
//				frecent.println(t);
//			}
//			catch (Exception e2) {
//				out = new File("/Volumes/Untitled/NeuralNetworks/Generation" + t + ".txt");
//				recent = new File("/Volumes/Untitled/NeuralNetworks/MostRecent.txt");
//				fout = new PrintWriter(out);
//				frecent = new PrintWriter(recent);
//				frecent.println(t);
//			}
//		}
		// the file is saved with the systems current time in milliseconds for easy sorting based on age and so each file has a unique name.
		
		// to save the millisecond timestamp of the most recent file for the loading method. This is obsolete and there is a better wau to do this, so I will be changing in the future
		
		
		// this whole loop saves each neural network in this format. NumLayers;NumNeuronsPerLayer;GenePatterns;Age;Fitness
		// when loaded each is split by the semicolon, Fitness and Age are for determining how well the algorithm is running and are not loaded.
		//NumLayers is a single integer
		//NumNeuronsPerLayer is formatted as this, NumNeuron1,NumNeurons2....,NumNeuronsNumLayers where each is the number of regular neurons in each layer;
		//Gene Patterns is formated as this inlayer_inneuron_outneuron_outlayer_weight_enabled,....inlayer_inneuron_outneuron_outlayer_weight_enabled. 
		//inlayer is the layer that the neuron outputting its data exists in. inneuron specifies the neuron in that layers. replacing in with out just changes it to the receiving layer and neuron.
		//weight is the weight of the gene/connection,and enabled determines whether the gene/connection is enabled or disabled with a 1 and 0 respectively.
		for (NeuralNetwork nn : nns){
			fout.print(nn.getLayers().size() + ";");
			for (Layer l : nn.getLayers()){
				fout.print(l.getNeurons().size() + ",");
				
			}
			fout.print(";");
			for (Layer l : nn.getLayers()){
				if (l.isInput()){
					int layernumber = l.getNumber();
					for (InputNeuron n : l.getINeurons()){
						String neurondata = n.getMarket().getMarketName() + "_" + n.getSelector();
						for (Gene g :n.getGenes()){
							Neuron nout = g.getConnection();
							if (nn.getLayers().size() == nout.getLayernumber()) {
								OutputNeuron nout2 = g.getOConnection();
								String noutnum = nout2.getMarket().getMarketName() + "_" + nout2.getSelector();
								int noutlayer = nout2.getLayernumber();
								double weight = g.getWeight();
								int enabled = g.getstate();
								fout.print(layernumber + ":" + neurondata + ":" + noutnum + ":" + noutlayer + ":" + weight + ":" + enabled + ",");
							}
							else{
								int noutnum = nout.getNumber();
								int noutlayer = nout.getLayernumber();
								double weight = g.getWeight();
								int enabled = g.getstate();
								fout.print(layernumber + ":" + neurondata + ":" + noutnum + ":" + noutlayer + ":" + weight + ":" + enabled + ",");
							}
							
						}
					}
				}
				else{
					int layernumber = l.getNumber();
					for (Neuron n : l.getNeurons()){
						int neuronnumber = n.getNumber();
						for (Gene g :n.getGenes()){
							Neuron nout = g.getConnection();
							if (nn.getLayers().size() == nout.getLayernumber()) {
								OutputNeuron nout2 = g.getOConnection();
								if(nout2 == null){
									n.RemoveGenes(g);
								}
								else{
								String noutnum = nout2.getMarket().getMarketName() + "_" + nout2.getSelector();
								int noutlayer = nout2.getLayernumber();
								double weight = g.getWeight();
								int enabled = g.getstate();
								fout.print(layernumber + ":" + neuronnumber + ":" + noutnum + ":" + noutlayer + ":" + weight + ":" + enabled + ",");
								}
							}
							else{
								int noutnum = nout.getNumber();
								int noutlayer = nout.getLayernumber();
								double weight = g.getWeight();
								int enabled = g.getstate();
								fout.print(layernumber + ":" + neuronnumber + ":" + noutnum + ":" + noutlayer + ":" + weight + ":" + enabled + ",");
							}
							
						}
					}
				}
				
			}
			int i = -1;
			while(!markets[++i].getMarketName().equals("USDT-BTC"));
			fout.println("; Created (Genarations Ago):" + nn.getAge() + "; Made (USD):" + ((nn.getFitness()-noact)*markets[i].getData(3))+ "; Global Error :" + nn.getGlobalError());
		}
		fout.close();
		try{
		sftpChannel.put("Generation.txt","Generation"+ t +".txt");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	//this is where the new populations are created.
	private static NeuralNetwork[] evolve(NeuralNetwork[] nns,Market[] markets) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
	//back propagation algorithm
	private static void backpropagate(NeuralNetwork[] nns) throws IOException {
		totalGlobalError = 0;
		for(NeuralNetwork nn : nns){
			Layer out = nn.getLayers().get(nn.getLayers().size()-1);
			ArrayList<OutputNeuron> sells = new ArrayList<OutputNeuron>();
			ArrayList<OutputNeuron> buys = new ArrayList<OutputNeuron>();
			// choose output neurons that should activate
			for(OutputNeuron n : out.getONeurons()){
				Method m = n.getOutputMethod();
				if (m.getName().contains("buy")){
					buys.add(n);
					n.setActive(false);
				}
				else{
					sells.add(n);
					n.setActive(false);
				}
			}
			OutputNeuron[] bestBuys = new OutputNeuron[10];
			for(int i = 0;i < buys.size(); i= i+5){
				for(int j = 0; j <10; j++){
					if(bestBuys[j] == null){
						if(buys.get(i).getMarket().getReturn() > 1){
						bestBuys[j] = buys.get(i+ rand.nextInt(4));
						j = 10;
						}
					}
					else if((bestBuys[j].getMarket().getReturn() < buys.get(i).getMarket().getReturn() && buys.get(i).getMarket().getReturn() > 1)|| buys.get(i).getMarket().marketName.equals("Test")){
						for(int k = 9; k > j; k--){
							bestBuys[k] = bestBuys[k-1];
						}
						bestBuys[j] = buys.get(i+rand.nextInt(4));
						j=10;
					}
				}				
			}
			for(OutputNeuron buy: bestBuys){
				if(buy != null)buy.setActive(true);
			}
			for(OutputNeuron sell : sells){
				if(sell.getMarket().getReturn() < 1){
					sell.setActive(true);
				}
			}
			// calculate the error for each output neuron
			for(OutputNeuron n : out.getONeurons()){
				if(n.getInputs().size() == 0) n.setError(0);
				else if(n.shouldAct && n.getLast() < 1)n.setError((1.01+Math.random()-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*(1-Sigmoid(n.getLast())));
				else if(!n.shouldAct && n.getLast() > 1)n.setError((-.01+Math.random()-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*(1-Sigmoid(n.getLast())));
				else n.setError(0);			
			}
			for(int i = nn.getLayers().size()-1; i > 0; i--){
				Layer l = nn.getLayers().get(i);
				if(l.isOutput()){
					//set weight for each output gene
					for(OutputNeuron n: l.getONeurons()){
						for(Gene g : n.getInputs()){
							Neuron input = g.getInput();
							if(input == null){
								InputNeuron inputI = g.getInputI();
								g.setLastChange(n.getError()*LEARNING_RATE*inputI.getLast()+g.getWeight()+g.getLastChange()*MOMENTUM);
								g.setWeight(g.getLastChange());
							}
							else{
								g.setLastChange(n.getError()*LEARNING_RATE*input.getLast()+g.getWeight()+g.getLastChange()*MOMENTUM);
								g.setWeight(g.getLastChange());
							}
						}
					}
				}
				else{
					for(Neuron n: l.getNeurons()){
						//calculate the error for Neurons
						double outputErrors = 0;
						for(Gene g : n.getGenes()){
							try{
								outputErrors = g.getConnection().getError();
							}
							catch(Exception e){
								outputErrors = g.getOConnection().getError();
							}
						}
						double expected = 1; // I need to figure out how to figure out the target, was not in documentation I read. Seems its supposed to be 1 but not sure;	t
						n.setError((expected-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*outputErrors);
						//adjust the weight for genes
						for(Gene g : n.getInputs()){
							Neuron input = g.getInput();
							if(input == null){
								InputNeuron inputI = g.getInputI();
								g.setLastChange(n.getError()*LEARNING_RATE*inputI.getLast()+g.getWeight()+g.getLastChange()*MOMENTUM);
								g.setWeight(g.getLastChange());
							}
							else{
								g.setLastChange(n.getError()*LEARNING_RATE*input.getLast()+g.getWeight()+g.getLastChange()*MOMENTUM);
								g.setWeight(g.getLastChange());
							}
						}
					}
				}
			}
			//calculate Global Error
			double totalsum = 0;
			for(Layer l : nn.getLayers()){
				double sum = 0;
				if (l.isOutput())for(OutputNeuron n : l.getONeurons())sum += n.getError();	
				else if(!l.isInput()) for(Neuron n : l.getNeurons())sum += n.getError();
				totalsum += Math.pow(sum, 2);
			}
			nn.setGlobalError(totalsum/2);
			totalGlobalError += totalsum/2;
		}		
	}
	private static double Sigmoid(double d) {
		return 1/(1+Math.exp(d*-1));
	}
	//Where cloning takes place
	private static NeuralNetwork clone(NeuralNetwork cloner) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		//starts by creating the basic structure for te new network
		Layer[] puts = creator(cloner.getMarkets());
		ArrayList<Layer> clonelayers = cloner.getLayers();
		NeuralNetwork newnn = new NeuralNetwork(puts[0],puts[1], cloner.getMarkets());
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
		Layer[] puts = creator(morefit.getMarkets());
		newnn = new NeuralNetwork(puts[0],puts[1],morefit.getMarkets());
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
		NeuralTracker(newnn);
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
	//This method make sure the numbers that each neuron holds showing its place in the neural network are correct because sometimes the order shifts around.
	private static void NeuralTracker(NeuralNetwork nn) {
		for (int i = 0; i < nn.getLayers().size(); i++){
			Layer l = nn.getLayers().get(i);
			l.setNumber(i+1);
			if (l.isInput()){
				ArrayList<InputNeuron> ns = l.getINeurons();
				for (int j = 0; j < ns.size(); j++){
					InputNeuron n = ns.get(j);
					for(Gene g : n.getGenes()) {
						g.setInputI(n);
					}
					n.setLayernumber(i+1);
					n.setNumber(j+1);
				}
			}
			else if (l.isOutput()){
				ArrayList<OutputNeuron> ns = l.getONeurons();
				for (int j = 0; j < ns.size(); j++){
					OutputNeuron n = ns.get(j);
					n.setLayernumber(i+1);
					n.setNumber(j+1);
				}
			}
			else {
				ArrayList<Neuron> ns = l.getNeurons();
				for (int j = 0; j < ns.size(); j++){
					Neuron n = ns.get(j);
					for(Gene g : n.getGenes()) {
						g.setInput(n);
					}
					n.setLayernumber(i+1);
					n.setNumber(j+1);
				}
			}	
		}
		
	}
	//This is where a single neural network is run
	private static void RunNetwork(NeuralNetwork nn) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//runs each layer in order
		nn.clearInputArrays();
		for (Layer l : nn.getLayers()){			
			if (l.isInput()){
				// gets the input data, and sends it to each connected neuron.
				for (InputNeuron n : l.getINeurons()){
					n.setValue(n.input());
					for (Gene g : n.getGenes()){
						Neuron connect = g.getConnection();
						double weight = g.getWeight();	
						double value = n.getValue();
						g.setLastInput(connect.getValue()+value*weight);
						connect.setValue(connect.getValue()+value*weight);
						try{
							g.getConnection().addInput(g);
							g.setInputI(n);
						}
						catch (Exception e){
							g.getOConnection().addInput(g);
							g.setInputI(n);
						}
					}
					n.setLast(n.getValue());
					n.setValue(0.0000001);
				}
			}
			else if (l.isOutput()){
				//calls the output methods if the data that passes all the way through is enough to trigger the output neuron.
				for (OutputNeuron n : l.getONeurons()){
					if (n.getValue() > .8){
						n.invoke();
					}
					n.setLast(n.getValue());
					n.setValue(0.01);
				}
			}
			else{
				//passes the data each neuron received onto the next neurons and resets its own data state.
				for (Neuron n : l.getNeurons()){
					for (Gene g : n.getGenes()){
						Neuron connect = g.getConnection();
						double weight = g.getWeight();
						double value = Sigmoid(n.getValue());
						g.setLastInput(connect.getValue()+value*weight);
						connect.setValue(connect.getValue()+value*weight);
						try{
							g.getConnection().addInput(g);
							g.setInput(n);
						}
						catch (Exception e){
							g.getOConnection().addInput(g);
							g.setInput(n);
						}
					}
					n.setLast(n.getValue());
					n.setValue(0.01);
				}
			}
		}
		
	}
	//creates all the markets
	public static Market[] marketCreator() throws IOException{
		//loads the bittrex api to get a list off all markets
		URL marketAPI = new URL("https://bittrex.com/api/v1.1/public/getmarketsummaries");
		BufferedReader in = new BufferedReader(new InputStreamReader(marketAPI.openStream()));
		String[] marketlist = in.readLine().substring(39).split("},");
		String[] names = new String[marketlist.length];
		for (int i = 0; i < marketlist.length;i++){
			String[] s2 = marketlist[i].split(",");
			names[i] = (s2[0].substring(15,s2[0].length()-1));
		}
		int banned = 0;
		for (String name : names){
			if (name.contains("ANS") || name.contains("SEC")){
				banned++;
			}
		}
		//creates the markets from the api includes some banned apis because they were broken.
		Market[] markets = new Market[names.length-banned];
		int j = 0;
		for (int i = 0; i < names.length ;i++){
			if (!names[i].contains("ANS") && !names[i].contains("SEC")){
				markets[i-j] = new Market(names[i]);
			}
			else j++;
		}
		return markets;
	}
	//builds the input and output layers for each neural network
	public static Layer[] creator(Market[] markets) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException{
		Layer[] layers = new Layer[2];
		
		Class<?> marketclass = Class.forName("Market");
		Method[] ms = marketclass.getMethods();
		Layer input = new Layer(true,false);
		Method m = null;
		for (Method method: ms){
			if (method.getName().equals("getData")){
				m = method;
			}
		}
		//creates 89 input neurons for each market for the 89 outputs of the get data method.
		for (Market market : markets){			
			for (int i = 0; i <= 89;i++){
				if (!market.getMarketName().equals("BTC-HKG")&&!market.getMarketName().equals("BTC-XBB")){
				InputNeuron in2 = new InputNeuron(m, market, i);
				input.addInputNeuron(in2);
				in2.setLayernumber(1);
				}
			}
		}
		for (InputNeuron n : input.getINeurons()){
			n.setLayernumber(1);
		}
		layers[0] = input;
		Layer output = new Layer(false,true);
		Class<?> outputs = Class.forName("OutputMethods");
		Method m1 = outputs.getMethods()[0];
		Method m2 = outputs.getMethods()[1];
		//creates 5 buy and 5 sell outputs for each market.
		for (Market market: markets){
			for (int i = 1; i <= 5;i++){
				if (!market.getMarketName().equals("BTC-HKG")&&!market.getMarketName().equals("BTC-XBB")){
				OutputNeuron out = new OutputNeuron(m1,1,market,i);
				output.addOutputNeuron(out);
				out.setLayernumber(2);
				out = new OutputNeuron(m2,1,market,i);
				output.addOutputNeuron(out);
				out.setLayernumber(2);
				}
			}
			
			
		}
		for(OutputNeuron n : output.getONeurons()){
			n.setLayernumber(2);
		}
		layers[1] = output;
		
		return layers;
		}
	//creates the basic networks or loads old ones onto the creator structure.
	public static NeuralNetwork[] CreateNetworks(int i, Market[] markets) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SftpException, JSchException{
			//if there are no load files , it creates one random gene for each neural network.
			NeuralNetwork[] NetworkList = new NeuralNetwork[i];
			
			try{
				File file = new File("Generation.txt");					
				load(NetworkList, file);
			
			}
			catch(Exception e){
			e.printStackTrace();
			for (int j = 0; j<i;j++){		
					Layer[] layers = creator(markets);
					Layer outputlayercopy = layers[1];
					Layer inputlayercopy = layers[0];
					outputlayercopy.setNumber(2);
					inputlayercopy.setNumber(1);
					int inputrand = 0;
					if(inputlayercopy.getINeurons().size()-1 >0) inputrand = rand.nextInt(inputlayercopy.getINeurons().size()-1);
					int outputrand = 0;
					if(outputlayercopy.getONeurons().size()-1 >0) outputrand = rand.nextInt(outputlayercopy.getONeurons().size()-1);
					Gene starter = new Gene(outputlayercopy.getONeurons().get(outputrand), (Math.random()*2)-1);
					inputlayercopy.getINeurons().get(inputrand).AddGenes(starter);
					starter.setInput(inputlayercopy.getINeurons().get(inputrand));
					NetworkList[j] = new NeuralNetwork(inputlayercopy, outputlayercopy, markets);			
					NetworkList[j] = new NeuralNetwork(inputlayercopy, outputlayercopy, markets);
					for (OutputNeuron n : outputlayercopy.getONeurons()){
							n.updateWallets(NetworkList[j].getWallets());
					}
					}									
			}
			return NetworkList;		
		}
	@SuppressWarnings("resource")
	//loads the most recent save file.
	private static void load(NeuralNetwork[] networkList, File f) throws IOException, SftpException, JSchException {
		//loads this to find the actual most recent network. may be obsolete.
		Scanner fin = new Scanner(f);	
		for (NeuralNetwork nn : networkList){
			
			String[] netData = fin.nextLine().split(";");
			// creates a layer based on the numlayer data point
			for (int i = 2; i < Integer.parseInt(netData[0]); i++){
				nn.addLayer(new Layer(false,false));
			}
			//creates neurons in each layer
			String[] NeuronData = netData[1].split(",");
			for (int i =1; i < nn.getLayers().size()-1; i++){
				for (int j = 0; j < Integer.parseInt(NeuronData[i]); j++){
					nn.getLayers().get(i).addNeuron(new Neuron());
				}
			}
			//creates the genes and adds them to each neuron.
			String[] GeneData = netData[2].split(",");
			NeuralTracker(nn);
			for(String data : GeneData){
				if(!data.equals("")){
				String[] g = data.split(":");
				int inLayer = Integer.parseInt(g[0]);
				Integer inNeuron = 0;
				if (inLayer == 1){
					String[] nData = g[1].split("_");
					boolean selected = false;
					for (InputNeuron n : nn.getLayers().get(0).getINeurons()){
						if (n.getMarket().getMarketName().equals(nData[0]) && n.getSelector() == Integer.parseInt(nData[1])){
							selected = true;
							inNeuron = n.getNumber();
						}
					}
					if(!selected)inNeuron = (Integer) null;
				}				
				else{
					try{
					inNeuron = Integer.parseInt(g[1]);
					}
					catch(Exception e){
						
					}
				}
				int outLayer = Integer.parseInt(g[3]);
				Integer  outNeuron = 0;
				if (outLayer == Integer.parseInt(netData[0])){
					String[] nData = g[2].split("_");
					boolean selected = false;
					for (OutputNeuron n : nn.getLayers().get(Integer.parseInt(netData[0])-1).getONeurons()){
						if (n.getMarket().getMarketName().equals(nData[0]) && n.getSelector() == Integer.parseInt(nData[1])){
							outNeuron = n.getNumber();
							selected = true;
						}
					}
					if(!selected)outNeuron = (Integer) null;;
				}
				else{
					outNeuron = Integer.parseInt(g[2]);
				}
				double weight = Double.parseDouble(g[4]);
				int enabled = 1;
				try {
					enabled = Integer.parseInt(g[5]);
				}
				catch (Exception e){
				}
				try{
				if(inLayer == 1 && inNeuron != null && outNeuron != null){
					if(outLayer == Integer.parseInt(netData[0])){
						Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getONeurons().get(outNeuron-1), weight);
						if (enabled == 0)g2.toggle();
						nn.getLayers().get(0).getINeurons().get(inNeuron-1).AddGenes(g2);
						g2.setInput(nn.getLayers().get(0).getINeurons().get(inNeuron-1));
						nn.getLayers().get(outLayer-1).getONeurons().get(outNeuron-1).addInput(g2);
					}
					else{
						Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1), weight);
						if (enabled == 0)g2.toggle();
						nn.getLayers().get(0).getINeurons().get(inNeuron-1).AddGenes(g2);
						g2.setInput(nn.getLayers().get(0).getINeurons().get(inNeuron-1));
						nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1).addInput(g2);
					}
				}
				else if(inNeuron != null && outNeuron != null){
					if(outLayer == Integer.parseInt(netData[0])){
						Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getONeurons().get(outNeuron-1), weight);
						if (enabled == 0)g2.toggle();
						nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1).AddGenes(g2);
						g2.setInput(nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1));
						nn.getLayers().get(outLayer-1).getONeurons().get(outNeuron-1).addInput(g2);
					}
					else{
						Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1), weight);
						if (enabled == 0)g2.toggle();
						nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1).AddGenes(g2);
						g2.setInput(nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1));
						nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1).addInput(g2);
					}
				}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				}
			}
		}
		
	}
	//creates threads that keep all the markets updated in real time.
	public static void updater (Market[] markets) throws IOException{
			ArrayList<Thread> threads = new ArrayList<Thread>();
			for (Market market : markets){
				threads.add (new Thread() {
					public void run(){
						try {
							updater(market);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				
			}
			for (Thread thread: threads){
				thread.start();
			}
			System.out.println("all started updating");
		}
	//keeps a single market constantly updated
	public static void updater (Market market) throws IOException{
		//had to modify this while loop this when the api was updating at some point and had null data in places. left it in for easy modification in future updates.
			while(!market.getMarketName().equals("BTC-HKG") && !market.getMarketName().equals("BTC-XBB")){
				market.updateMarkets();
			}
		}
	// does the same thing as the last neural tracker but does it for an array of neuralnetworks instead of just one.
	public static void Neuraltracker(NeuralNetwork[] nns){
		for (NeuralNetwork nn : nns){
			for (int i = 0; i < nn.getLayers().size(); i++){
				Layer l = nn.getLayers().get(i);
				l.setNumber(i+1);
				if (l.isInput()){
					ArrayList<InputNeuron> ns = l.getINeurons();
					for (int j = 0; j < ns.size(); j++){
						InputNeuron n = ns.get(j);
						n.setLayernumber(i+1);
						n.setNumber(j+1);
					}
				}
				else if (l.isOutput()){
					ArrayList<OutputNeuron> ns = l.getONeurons();
					for (int j = 0; j < ns.size(); j++){
						OutputNeuron n = ns.get(j);
						n.setLayernumber(i+1);
						n.setNumber(j+1);
					}
				}
				else {
					ArrayList<Neuron> ns = l.getNeurons();
					for (int j = 0; j < ns.size(); j++){
						Neuron n = ns.get(j);
						n.setLayernumber(i+1);
						n.setNumber(j+1);
					}
				}
			
				
				
				
			}
		}
		
	}
	//runs the current best neural network constantly,  to see how the neuralnetwork is currently running.
	public static void main(Market[] markets) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SftpException {
		Layer[] l = creator(markets);
		NeuralNetwork noact = new NeuralNetwork(l[0],l[1], markets);
		for(OutputNeuron n : noact.getLayers().get(noact.getLayers().size()-1).getONeurons()){
			n.updateWallets(noactwallets);
		}
		while(true){
		l = creator(markets);
		NeuralNetwork nn = new NeuralNetwork(l[0],l[1], markets);		
		File f = new File("Generation.txt");
		@SuppressWarnings("resource")
		Scanner fin = new Scanner(f);
		String[] netData = fin.nextLine().split(";");
		for (int i = 2; i < Integer.parseInt(netData[0]); i++){
			nn.addLayer(new Layer(false,false));
		}
		String[] NeuronData = netData[1].split(",");
		for (int i =1; i < nn.getLayers().size()-1; i++){
			for (int j = 0; j < Integer.parseInt(NeuronData[i]); j++){
				nn.getLayers().get(i).addNeuron(new Neuron());
			}
		}
		String[] GeneData = netData[2].split(",");
		for(String data : GeneData){
			if(!data.equals("")){
			String[] g = data.split(":");
			int inLayer = Integer.parseInt(g[0]);
			Integer inNeuron = 0;
			if (inLayer == 1){
				String[] nData = g[1].split("_");
				boolean selected = false;
				for (InputNeuron n : nn.getLayers().get(0).getINeurons()){
					if (n.getMarket().getMarketName().equals(nData[0]) && n.getSelector() == Integer.parseInt(nData[1])){
						selected = true;
						inNeuron = n.getNumber();
					}
				}
				if(!selected)inNeuron = (Integer) null;
			}
			
			else{
				inNeuron = Integer.parseInt(g[1]);
			}
			int outLayer = Integer.parseInt(g[3]);
			Integer  outNeuron = 0;
			if (outLayer == Integer.parseInt(netData[0])){
				String[] nData = g[2].split("_");
				boolean selected = false;
				for (OutputNeuron n : nn.getLayers().get(Integer.parseInt(netData[0])-1).getONeurons()){
					if (n.getMarket().getMarketName().equals(nData[0]) && n.getSelector() == Integer.parseInt(nData[1])){
						outNeuron = n.getNumber();
						selected = true;
					}
				}
				if(!selected)outNeuron = (Integer) null;;
			}
			else{
				outNeuron = Integer.parseInt(g[2]);
			}
			double weight = Double.parseDouble(g[4]);
			int enabled = 1;
			try {
				enabled = Integer.parseInt(g[5]);
			}
			catch (Exception e){
			}
			try{
			if(inLayer == 1){
				if(outLayer == Integer.parseInt(netData[0])){
					Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getONeurons().get(outNeuron-1), weight);
					if (enabled == 0)g2.toggle();
					nn.getLayers().get(0).getINeurons().get(inNeuron-1).AddGenes(g2);
				}
				else{
					Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1), weight);
					if (enabled == 0)g2.toggle();
					nn.getLayers().get(0).getINeurons().get(inNeuron-1).AddGenes(g2);
				}
			}
			else{
				if(outLayer == Integer.parseInt(netData[0])){
					Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getONeurons().get(outNeuron-1), weight);
					if (enabled == 0)g2.toggle();
					nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1).AddGenes(g2);
				}
				else{
					Gene g2 = new Gene(nn.getLayers().get(outLayer-1).getNeurons().get(outNeuron-1), weight);
					if (enabled == 0)g2.toggle();
					nn.getLayers().get(inLayer-1).getNeurons().get(inNeuron-1).AddGenes(g2);
				}
			}
			}
			catch(Exception e){
				
			}
			}
		}
		for(OutputNeuron n : nn.getLayers().get(nn.getLayers().size()-1).getONeurons()){
			n.updateWallets(wallets);
		}
			long t1 = System.currentTimeMillis();
			long t2 = System.currentTimeMillis();
			while(t2-t1 < 60000){
			for (Layer l1 : nn.getLayers()){
				if (l1.isInput()){
					for (InputNeuron n : l1.getINeurons()){
						n.setValue(n.input());
						for (Gene g : n.getGenes()){
							Neuron connect = g.getConnection();
							double weight = g.getWeight();
							double value = n.getValue();
							connect.setValue(connect.getValue()+value*weight);
						}
						n.setValue(0);
					}
				}
				else if (l1.isOutput()){
					for (OutputNeuron n : l1.getONeurons()){
						if (n.getValue() > n.getActivation()){
							n.invoke();
						}
						n.setValue(0);
					}
				}
				else{
					for (Neuron n : l1.getNeurons()){
						for (Gene g : n.getGenes()){
							Neuron connect = g.getConnection();
							double weight = g.getWeight();
							double value = n.getValue();
							connect.setValue(connect.getValue()+value*weight);
						}
						n.setValue(0);
					}
				}
			}
			t2 = System.currentTimeMillis();
			}
			File f2 = new File("profit.txt");
			PrintWriter fout = new PrintWriter(f2);
			int i = -1;
			while(!markets[++i].getMarketName().equals("USDT-BTC"));
			fout.println((nn.getFitness()-noact.getFitness())*markets[i].getData(3));
			fout.close();
			try{
				sftpChannel.put("profit.txt","profit.txt");
		 	}
			catch(Exception e){
				
			}
			
		}
		
		
	}
	
}

