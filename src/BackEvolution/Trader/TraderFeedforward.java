package BackEvolution.Trader;

import java.io.IOException;
import java.util.Arrays;

import FeedForward.Feedforward;
import General.DataManager;
import NeuralNetwork.NeuralNetwork;

public class TraderFeedforward extends Feedforward {

	public TraderFeedforward(DataManager data) {
		super(data);
	}
	@Override
	public void EvolveSetup() {
		NeuralNetwork[] nns = data.getNetworks();
		for (NeuralNetwork nno : nns){
			TraderNetwork nn = (TraderNetwork) nno;
			nn.restartWallets();
		}
		data.getWriter().println("Wallets Restarted");
	}
	@Override
	public void EvolveTeardown() {
		TraderDataManager data1 = (TraderDataManager) data;
		NeuralNetwork[] nns = data.getNetworks();
		//Updates the fitness for each neural network 
		for (NeuralNetwork nno : nns){
			TraderNetwork nn = (TraderNetwork) nno;
			nn.updateFitness();		
		}
		data.getWriter().println("Fitness Determined");
		// determines the fitness if no action was taken
		double noact = 0;
		for (Wallet w : data1.getNoActWallets()){
			double amt = w.getAmmount();
			if (w.getName().equals("BTC")){
				noact+= amt;
			}
			else if (!w.getName().equals("XBB")&&!w.getName().equals("HKG")){
				for (Market market : data1.getMarkets()){
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
		data1.setNoAct(noact);
		// sorts the neural networks from most fit to least fit.
		Arrays.sort(nns);

	}
	@Override
	public void BackpropagationSetup() {
		for (NeuralNetwork nno : data.getNetworks()){
			TraderNetwork nn = (TraderNetwork) nno;
			nn.restartWallets();
		}
	}
	@Override
	public void BackIterationHandling() {
		TraderDataManager data1 = (TraderDataManager) data;
		Market[] markets = data1.getMarkets();
		for(Market m: markets){
			m.setOld();
		}

	}

}
