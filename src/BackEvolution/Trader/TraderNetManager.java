package BackEvolution.Trader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import BackEvolution.Layer;
import BackEvolution.NeuralNetwork;
import BackEvolution.Neuron;
import BackEvolution.SpecialNetManager;

public class TraderNetManager implements SpecialNetManager {
	private static TraderSingleton s = TraderSingleton.getInstance();
	private static ArrayList<Wallet> noactwallets;
	public static Random rand = new Random();
	private static int noact;
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
	// Don't Include semicolons, or colons in this return
	@Override
	public String saveInput(Neuron outo) {
		TraderNeuron out = (TraderNeuron) outo;
		return out.getMarket().getMarketName() + "_" + out.getSelector();
	}
	// Don't Include semicolons, or colons in this return
	@Override
	public String saveOutput(Neuron ino) {
		TraderNeuron in = (TraderNeuron) ino;
		return in.getMarket().getMarketName() + "_" + in.getSelector();
	}
	@Override
	public String saveMetaData(NeuralNetwork nn) {
		Market[] markets = s.getMarkets();
		int i = -1;
		while(!markets[++i].getMarketName().equals("USDT-BTC"));
		try {
			return "; Created (Genarations Ago):" + nn.getAge() + "; Made (USD):" + ((nn.getFitness()-noact)*markets[i].getData(3))+ "; Global Error :" + nn.getGlobalError();
		} catch (IOException e) {
			return "; Created (Genarations Ago):" + nn.getAge() + "; Global Error :" + nn.getGlobalError();
		}
	}
	
	
}
