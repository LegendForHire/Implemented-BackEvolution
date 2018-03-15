package BackEvolution.Trader;
import BackEvolution.Layer;
import BackEvolution.NetworkCreator;
import BackEvolution.NeuralNetwork;
import BackEvolution.Neuron;
import BackEvolution.SpecialCreator;

public class TraderCreator implements SpecialCreator {
	public static TraderSingleton s;
	public TraderCreator(){
		s = TraderSingleton.getInstance();
	}
	@Override
	public void NeuronSetup(Neuron no, int j) {
		TraderNeuron n = (TraderNeuron) no;
		n.updateWallets(((TraderNetwork)s.getNetworks()[j]).getWallets());
	}
	@Override
	public void InputOutputcreator(Layer[] layers){		
		Market[] markets = s.getMarkets();		
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
	@Override
	public void load(NeuralNetwork nno, String[] netData){
		//loads this to find the actual most recent network. may be obsolete.
			TraderNetwork nn = (TraderNetwork) nno;
			String[] GeneData = netData[2].split(",");
			for(String data : GeneData){
				if(!data.equals("")){
				String[] g = data.split(":");
				int inLayer = Integer.parseInt(g[0]);
				Integer inNeuron = 0;
				if (inLayer == 1){
					String[] nData = g[1].split("_");
					boolean selected = false;
					for (Neuron no : nn.getLayers().get(0).getNeurons()){
						TraderNeuron n= (TraderNeuron) no;
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
					for (Neuron no : nn.getLayers().get(Integer.parseInt(netData[0])-1).getNeurons()){
						TraderNeuron n = (TraderNeuron) no;
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
				if(inNeuron != null && outNeuron != null){
					NetworkCreator.geneAdder(weight,inLayer,outLayer,inNeuron,outNeuron,nn,enabled);						
				}						
				}
			}
		}
}
