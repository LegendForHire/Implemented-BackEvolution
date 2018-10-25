package BackEvolution.Trader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import General.DataManager;
import General.NeuralNetManager;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.Neuron;

public class TraderNetManager extends NeuralNetManager {


	public TraderNetManager(DataManager data) {
		super(data);
	}

	@Override
	public void setup() {
		try {
			TraderDataManager data1 = (TraderDataManager) data;
			ArrayList<Wallet> noactwallets = new ArrayList<Wallet>();
			URL currencies;
			currencies = new URL("https://bittrex.com/api/v1.1/public/getCurrencies");
			BufferedReader in = new BufferedReader(new InputStreamReader(currencies.openStream()));
			String[] currencyList = in.readLine().substring(39).split("},");
			for (String c : currencyList){
				c = c.substring(13);
				c = c.replace(c.substring(c.indexOf("\"")), "");
				noactwallets.add(new Wallet(c, 50));
			}
			data1.setNoActWallets(noactwallets);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	@Override
	public String saveInput(Neuron outo) {
		TraderNeuron out = (TraderNeuron) outo;
		return out.getMarket().getMarketName() + "_" + out.getSelector();
	}
	// Don't Include semicolons, or colons in this return
	@Override
	public String saveOutput(Neuron ino) {;
		TraderNeuron in = (TraderNeuron) ino;
		return in.getMarket().getMarketName() + "_" + in.getSelector();
	}
	@Override
	public String saveMetaData(NeuralNetwork nn) {
		TraderDataManager data1 = (TraderDataManager) data;
		Market[] markets = data1.getMarkets();
		int i = -1;
		while(!markets[++i].getMarketName().equals("USDT-BTC"));
		try {
			return  "; Made (USD):" + ((nn.getFitness()-data1.getNoAct())*markets[i].getData(3))+ "; Global Error :" + nn.getGlobalError();
		} catch (IOException e) {
			return "; Global Error :" + nn.getGlobalError();
			
		}
	}

}
