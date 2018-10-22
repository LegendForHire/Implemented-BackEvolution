package BackEvolution.Trader;

import java.util.ArrayList;
import java.util.Random;

import Backpropagate.Backpropagate;
import General.DataManager;
import General.Layer;
import General.NeuralNetwork;
import General.Neuron;
import General.PropertyReader;

public class TraderBackpropagate extends Backpropagate {

	public TraderBackpropagate(DataManager data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setAct() {
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

}
