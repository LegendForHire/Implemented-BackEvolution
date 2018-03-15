package BackEvolution.Trader;

import java.util.ArrayList;
import java.util.Random;

import BackEvolution.Layer;
import BackEvolution.NeuralNetwork;
import BackEvolution.Neuron;
import BackEvolution.ShouldAct;

public class TraderAct implements ShouldAct {

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
			else{
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
			if(sell.getMarket().getReturn() < 1){
				sell.setActive(true);
			}
		}
		
	}
	}
	
}
