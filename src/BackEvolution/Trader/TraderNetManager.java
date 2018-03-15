package BackEvolution.Trader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import BackEvolution.Gene;
import BackEvolution.Layer;
import BackEvolution.NeuralNetwork;
import BackEvolution.Neuron;
import BackEvolution.SpecialNetManager;

public class TraderNetManager implements SpecialNetManager {
	private static TraderSingleton s = TraderSingleton.getInstance();
	private static ArrayList<Wallet> noactwallets;
	public static Random rand = new Random();
	private static int noact;
	public static final double ALLOWABLE_ERROR= 250;
	public static final int TIMING = 60000;
	public static final int NUM_NETWORKS = 200;
	public static final double ACTIVATION = .8;
	public TraderNetManager(){}
	public static final double LEARNING_RATE = .01;
	public static final double MOMENTUM = .25;
	public void setup(){	
		try {
			TraderSingleton s = TraderSingleton.getInstance();
			s.setAllowed(ALLOWABLE_ERROR);
			s.setTiming(TIMING);
			s.setActivation(ACTIVATION);
			s.setNumNetworks(NUM_NETWORKS);
			s.setLearningRate(LEARNING_RATE);
			s.setMomentum(MOMENTUM);
			noactwallets = new ArrayList<Wallet>();
			URL currencies;
			currencies = new URL("https://bittrex.com/api/v1.1/public/getCurrencies");
			BufferedReader in = new BufferedReader(new InputStreamReader(currencies.openStream()));
			String[] currencyList = in.readLine().substring(39).split("},");
			for (String c : currencyList){
				c = c.substring(13);
				c = c.replace(c.substring(c.indexOf("\"")), "");
				noactwallets.add(new Wallet(c, 50));
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void IterativeSetup() {
		for (NeuralNetwork nno : s.getNetworks()){
			TraderNetwork nn = (TraderNetwork) nno;
			nn.restartWallets();
		}
	}
	public void BackIterationHandling(){
		Market[] markets = TraderSingleton.getInstance().getMarkets();
		for(Market m: markets){
			m.setOld();
		}
	}
	public void EvolveSetup(){
		NeuralNetwork[] nns = s.getNetworks();
		for (NeuralNetwork nno : nns){
			TraderNetwork nn = (TraderNetwork) nno;
			nn.restartWallets();
			
		}
		s.getWriter().println("Wallets Restarted");
	}
	public void EvolveTeardown(){
		NeuralNetwork[] nns = s.getNetworks();
		//Updates the fitness for each neural network 
		for (NeuralNetwork nno : nns){
			TraderNetwork nn = (TraderNetwork) nno;
			try {
				nn.updateFitness();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
		s.getWriter().println("Fitness Determined");
		// determines the fitness if no action was taken
		noact = 0;
		for (Wallet w : noactwallets){
			double amt = w.getAmmount();
			if (w.getName().equals("BTC")){
				noact+= amt;
			}
			else if (!w.getName().equals("XBB")&&!w.getName().equals("HKG")){
				for (Market market : s.getMarkets()){
					if (market.getMarketName().equals("BTC-" + w.getName())){
						try {
							noact += amt*market.getData(3);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				
				}
			}
		}
		// sorts the neural networks from most fit to least fit.
		Arrays.sort(nns);
	}
	public void save(){
		NeuralNetwork[] nns = s.getNetworks();
		Market[] markets = s.getMarkets();
		
		long t = System.currentTimeMillis();
		File out;
		File recent;
		PrintWriter fout;
		PrintWriter frecent;

			out = new File("Generation.txt");
			recent = new File("MostRecent.txt");
			try {
				fout = new PrintWriter(out);
				frecent = new PrintWriter(recent);
				frecent.println(t);
				frecent.close();
	
		
		for (NeuralNetwork nn : nns){
			fout.print(nn.getLayers().size() + ";");
			for (Layer l : nn.getLayers()){
				fout.print(l.getNeurons().size() + ",");
				
			}
			fout.print(";");
			for (Layer l : nn.getLayers()){
				if (l.isInput()){
					int layernumber = l.getNumber();
					for (Neuron no : l.getNeurons()){
						TraderNeuron n = (TraderNeuron) no;
						String neurondata = n.getMarket().getMarketName() + "_" + n.getSelector();
						for (Gene g :n.getGenes()){
							Neuron nout = g.getConnection();
							if (nn.getLayers().size() == nout.getLayernumber()) {
								Neuron nout2o = g.getConnection();
								TraderNeuron nout2 = (TraderNeuron) nout2o;
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
								Neuron nout2o = g.getConnection();
								TraderNeuron nout2 = (TraderNeuron) nout2o;
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
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void setAct() {
	Random rand = new Random();
	TraderSingleton s = TraderSingleton.getInstance();
	for(NeuralNetwork nn : s.getNetworks()){
		Layer out = nn.getLayers().get(nn.getLayers().size()-1);
		ArrayList<Neuron> sells = new ArrayList<Neuron>();
		ArrayList<Neuron> buys = new ArrayList<Neuron>();
		
		for(Neuron n : out.getNeurons()){
			String m = n.getOutputMethod();
			if (m.contains("buy")){
				buys.add(n);
				n.setActive(false);
			}
			else if(m.contains("sell")){
				sells.add(n);
				n.setActive(false);
			}
		}
		Neuron[] bestBuys = new Neuron[10];
		for(int i = 0;i < buys.size(); i= i+5){
			for(int j = 0; j <10; j++){
				if(bestBuys[j] == null){
					if(((TraderNeuron)buys.get(i)).getMarket().getReturn() > 1){
					bestBuys[j] = buys.get(i+ rand.nextInt(4));
					j = 10;
					}
				}
				else if((((TraderNeuron)bestBuys[j]).getMarket().getReturn() < ((TraderNeuron)buys.get(i)).getMarket().getReturn() && ((TraderNeuron)buys.get(i)).getMarket().getReturn() > 1)){
					for(int k = 9; k > j; k--){
						bestBuys[k] = bestBuys[k-1];
					}
					bestBuys[j] = buys.get(i+rand.nextInt(4));
					j=10;
				}
			}				
		}
		for(Neuron buy: bestBuys){
			if(buy != null)buy.setActive(true);
		}
		for(Neuron sello : sells){
			TraderNeuron sell = (TraderNeuron) sello;
			Market m = sell.getMarket();
			if(m.getReturn() < 1){
				sell.setActive(true);
			}
		}		
	}
	}
	
	
}
