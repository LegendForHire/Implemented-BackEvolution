package BackEvolution.Trader;

import General.DataManager;
import General.Layer;
import General.NetworkCreator;
import General.Neuron;

public class TraderCreator extends NetworkCreator {

	public TraderCreator(DataManager data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void NeuronSetup(Neuron no, int j) {
		TraderNeuron n = (TraderNeuron) no;
		n.updateWallets(((TraderNetwork)data.getNetworks()[j]).getWallets());	
	}
	@Override
	public void InputOutputcreator(Layer[] layers) {
		TraderDataManager data1 = (TraderDataManager) data;
		Market[] markets = data1.getMarkets();		
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
