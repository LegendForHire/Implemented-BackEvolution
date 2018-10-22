package BackEvolution.Trader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;

import General.DataManager;
import General.Layer;
import General.NeuralNetwork;
import General.Neuron;

public class TraderNetwork extends NeuralNetwork{
	private Market[] markets;
	public ArrayList<Wallet> wallets;
	public TraderNetwork(Layer inputLayer, Layer outputLayer, DataManager data1) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		super(inputLayer, outputLayer, new TraderLayer(false,false).getClass(), data1);
		TraderDataManager data = (TraderDataManager) data1;
		this.markets = data.getMarkets();
		URL currencies = new URL("https://bittrex.com/api/v1.1/public/getCurrencies");
		wallets = new ArrayList<Wallet>();
		BufferedReader in = new BufferedReader(new InputStreamReader(currencies.openStream()));
		String[] currencyList = in.readLine().substring(39).split("},");
		//creates default wallets for the network
		for (String c : currencyList){
			c = c.substring(13);
			c = c.replace(c.substring(c.indexOf("\"")), "");
			Wallet w = new Wallet(c, 50);
			wallets.add(w);
		}
	}
	public TraderNetwork(TraderNetwork nn) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException{
		super(nn,new TraderLayer(false,false).getClass());
		restartWallets();
		updateFitness();
		markets = nn.getMarkets();
		URL currencies = new URL("https://bittrex.com/api/v1.1/public/getCurrencies");
		wallets = new ArrayList<Wallet>();
		BufferedReader in = new BufferedReader(new InputStreamReader(currencies.openStream()));
		String[] currencyList = in.readLine().substring(39).split("},");
		//creates default wallets for the network
		for (String c : currencyList){
			c = c.substring(13);
			c = c.replace(c.substring(c.indexOf("\"")), "");
			Wallet w = new Wallet(c, 50);
			wallets.add(w);
		}
	}
	public void setMarkets(Market[] markets){
		this.markets= markets;
	}
	public Market[] getMarkets(){
		return markets;
	}
	public void restartWallets(){
		try{
		wallets.clear();
		URL currencies = new URL("https://bittrex.com/api/v1.1/public/getCurrencies");
		BufferedReader in = new BufferedReader(new InputStreamReader(currencies.openStream()));
		String[] currencyList = in.readLine().substring(39).split("},");
		for (String c : currencyList){
			c = c.substring(13);
			c = c.replace(c.substring(c.indexOf("\"")), "");
			wallets.add(new Wallet(c, 50));
		}
		for (Neuron no : layers.get(layers.size()-1).getNeurons()){
			TraderNeuron n = (TraderNeuron) no;
			n.updateWallets(wallets);
		}
		}
		catch (Exception e){
			long t = System.currentTimeMillis();
			while (System.currentTimeMillis() - t < 10000);
			restartWallets();
		}
	}
	@Override
	public void updateFitness(){
		fitness = 0;
		for (Wallet w : wallets){
			double amt = w.getAmmount();
			if (w.getName().equals("BTC")){
				fitness+= amt;
			}
			else if(!w.getName().equals("XBB")&&!w.getName().equals("HKG")){
				for (Market market : markets){
					if (market.getMarketName().equals("BTC-" + w.getName())){
						try {
							fitness += amt*market.getData(3);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				
				}
			}
		}
	}
	public ArrayList<Wallet> getWallets(){
		return wallets;
	}
	public void addWallet(Wallet w){
		wallets.add(w);
	}
	public Wallet getWallet(String currency){
		for (Wallet w : wallets){
			if (w.getName().equals(currency)) return w;
		}
		return null;
	}
}
