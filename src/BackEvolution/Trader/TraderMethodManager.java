package BackEvolution.Trader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import General.DataManager;
import General.Layer;
import General.MethodManager;
import General.NeuralNetwork;
import General.Neuron;
import General.PropertyReader;

public class TraderMethodManager extends MethodManager {
	private static ArrayList<Wallet> noactwallets;
	public static Random rand = new Random();
	private static int noact;
	public TraderMethodManager() {
		super();
	}
	public void setup(){	
		try {
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
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	@Override
	public void BackpropagationSetup(DataManager data) {
		for (NeuralNetwork nno : data.getNetworks()){
			TraderNetwork nn = (TraderNetwork) nno;
			nn.restartWallets();
		}
	}
	public void BackIterationHandling(DataManager data1){
		TraderDataManager data = (TraderDataManager) data1;
		Market[] markets = data.getMarkets();
		for(Market m: markets){
			m.setOld();
		}
	}
	public void EvolveSetup(DataManager data){
		NeuralNetwork[] nns = data.getNetworks();
		for (NeuralNetwork nno : nns){
			TraderNetwork nn = (TraderNetwork) nno;
			nn.restartWallets();
			
		}
		data.getWriter().println("Wallets Restarted");
	}
	public void EvolveTeardown(DataManager data1) {
		TraderDataManager data = (TraderDataManager) data1;
		NeuralNetwork[] nns = data.getNetworks();
		//Updates the fitness for each neural network 
		for (NeuralNetwork nno : nns){
			TraderNetwork nn = (TraderNetwork) nno;
			nn.updateFitness();		
		}
		data.getWriter().println("Fitness Determined");
		// determines the fitness if no action was taken
		noact = 0;
		for (Wallet w : noactwallets){
			double amt = w.getAmmount();
			if (w.getName().equals("BTC")){
				noact+= amt;
			}
			else if (!w.getName().equals("XBB")&&!w.getName().equals("HKG")){
				for (Market market : data.getMarkets()){
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
	@Override
	public void setAct(DataManager data) {
	long t1 = System.currentTimeMillis();
	while(System.currentTimeMillis() - t1 < Long.parseLong(PropertyReader.getProperty("timing")));
	Random rand = new Random();
	for(NeuralNetwork nn : data.getNetworks()){
		Layer out = nn.getLayers().get(nn.getLayers().size()-1);
		ArrayList<Neuron> sells = new ArrayList<Neuron>();
		ArrayList<Neuron> buys = new ArrayList<Neuron>();
		
		for(Neuron n : out.getNeurons()){
			String m = n.getMethod();
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
	// Don't Include semicolons, or colons in this return
	@Override
	public String saveInput(Neuron outo, DataManager data1) {
		TraderNeuron out = (TraderNeuron) outo;
		return out.getMarket().getMarketName() + "_" + out.getSelector();
	}
	// Don't Include semicolons, or colons in this return
	@Override
	public String saveOutput(Neuron ino, DataManager data1) {;
		TraderNeuron in = (TraderNeuron) ino;
		return in.getMarket().getMarketName() + "_" + in.getSelector();
	}
	@Override
	public String saveMetaData(NeuralNetwork nn, DataManager data1) {
		TraderDataManager data = (TraderDataManager) data1;
		Market[] markets = data.getMarkets();
		int i = -1;
		while(!markets[++i].getMarketName().equals("USDT-BTC"));
		try {
			return  "; Made (USD):" + ((nn.getFitness()-noact)*markets[i].getData(3))+ "; Global Error :" + nn.getGlobalError();
		} catch (IOException e) {
			return "; Global Error :" + nn.getGlobalError();
			
		}
	}
	@Override
	public void setup(DataManager data) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public DataManager SetupStartup() {
		TraderDataManager data = new TraderDataManager(this);
		try {
			MarketManager.start(data);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return data;
	}
	@Override
	public void AfterStartup(DataManager data) {
		try {
			ProgressTracker.start((TraderDataManager) data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void NeuronSetup(Neuron no, int j, DataManager data) {
		TraderNeuron n = (TraderNeuron) no;
		n.updateWallets(((TraderNetwork)data.getNetworks()[j]).getWallets());	
	}
	@Override
	public void InputOutputcreator(Layer[] layers, DataManager data1) {
		TraderDataManager data = (TraderDataManager) data1;
		Market[] markets = data.getMarkets();		
		for (Market market: markets){
			if(market != null){
			for (int i = 0; i <= 89;i++){
				if (!market.getMarketName().equals("BTC-HKG")&&!market.getMarketName().equals("BTC-XBB")){
				TraderNeuron in = new TraderNeuron("input", market, i);
				layers[0].addNeuron(in);
				in.setLayernumber(1);
				}
			}
			for (int i = 1; i <= 5;i++){
				if (!market.getMarketName().equals("BTC-HKG")&&!market.getMarketName().equals("BTC-XBB")){
				Neuron out = new TraderNeuron("buy",market,i);
				layers[1].addNeuron(out);
				out.setLayernumber(2);
				}
			}
			Neuron out = new TraderNeuron("sell",market,5);
			layers[1].addNeuron(out);
			out.setLayernumber(2);
			}
		}
		
	}
}
