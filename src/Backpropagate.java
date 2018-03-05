import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Backpropagate {
	public static final double LEARNING_RATE = .01;
	public static final double MOMENTUM = .25;

	public static Singleton s = Singleton.getInstance();
	public static Random rand = new Random();
	public static void backpropagate(NeuralNetwork[] nns) throws IOException {
		s.setTotalGlobalError(0.0);
		for(NeuralNetwork nn : nns){
			Layer out = nn.getLayers().get(nn.getLayers().size()-1);
			ArrayList<OutputNeuron> sells = new ArrayList<OutputNeuron>();
			ArrayList<OutputNeuron> buys = new ArrayList<OutputNeuron>();
			// choose output neurons that should activate
			for(OutputNeuron n : out.getONeurons()){
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
			OutputNeuron[] bestBuys = new OutputNeuron[10];
			for(int i = 0;i < buys.size(); i= i+5){
				for(int j = 0; j <10; j++){
					if(bestBuys[j] == null){
						if(buys.get(i).getMarket().getReturn() > 1){
						bestBuys[j] = buys.get(i+ rand.nextInt(4));
						j = 10;
						}
					}
					else if((bestBuys[j].getMarket().getReturn() < buys.get(i).getMarket().getReturn() && buys.get(i).getMarket().getReturn() > 1)|| buys.get(i).getMarket().marketName.equals("Test")){
						for(int k = 9; k > j; k--){
							bestBuys[k] = bestBuys[k-1];
						}
						bestBuys[j] = buys.get(i+rand.nextInt(4));
						j=10;
					}
				}				
			}
			for(OutputNeuron buy: bestBuys){
				if(buy != null)buy.setActive(true);
			}
			for(OutputNeuron sell : sells){
				if(sell.getMarket().getReturn() < 1){
					sell.setActive(true);
				}
			}
			// calculate the error for each output neuron
			for(OutputNeuron n : out.getONeurons()){
				if(n.getInputs().size() == 0) n.setError(0);
				else if(n.shouldAct && n.getLast() < 1)n.setError((1.01+Math.random()-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*(1-Sigmoid(n.getLast())));
				else if(!n.shouldAct && n.getLast() > 1)n.setError((-.01+Math.random()-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*(1-Sigmoid(n.getLast())));
				else n.setError(0);			
			}
			for(int i = nn.getLayers().size()-1; i > 0; i--){
				Layer l = nn.getLayers().get(i);
				if(l.isOutput()){
					//set weight for each output gene
					for(OutputNeuron n: l.getONeurons()){
						for(Gene g : n.getInputs()){
							Neuron input = g.getInput();
							if(input == null){
								InputNeuron inputI = g.getInputI();
								g.setLastChange(n.getError()*LEARNING_RATE*inputI.getLast()+g.getWeight()+g.getLastChange()*MOMENTUM);
								g.setWeight(g.getLastChange());
							}
							else{
								g.setLastChange(n.getError()*LEARNING_RATE*input.getLast()+g.getWeight()+g.getLastChange()*MOMENTUM);
								g.setWeight(g.getLastChange());
							}
						}
					}
				}
				else{
					for(Neuron n: l.getNeurons()){
						//calculate the error for Neurons
						double outputErrors = 0;
						for(Gene g : n.getGenes()){
							try{
								outputErrors = g.getConnection().getError();
							}
							catch(Exception e){
								outputErrors = g.getOConnection().getError();
							}
						}
						double expected = 1; // I need to figure out how to figure out the target, was not in documentation I read. Seems its supposed to be 1 but not sure;	t
						n.setError((expected-Sigmoid(n.getLast()))*Sigmoid(n.getLast())*outputErrors);
						//adjust the weight for genes
						for(Gene g : n.getInputs()){
							Neuron input = g.getInput();
							if(input == null){
								InputNeuron inputI = g.getInputI();
								g.setLastChange(n.getError()*LEARNING_RATE*inputI.getLast()+g.getWeight()+g.getLastChange()*MOMENTUM);
								g.setWeight(g.getLastChange());
							}
							else{
								g.setLastChange(n.getError()*LEARNING_RATE*input.getLast()+g.getWeight()+g.getLastChange()*MOMENTUM);
								g.setWeight(g.getLastChange());
							}
						}
					}
				}
			}
			//calculate Global Error
			double totalsum = 0;
			for(Layer l : nn.getLayers()){
				double sum = 0;
				if (l.isOutput())for(OutputNeuron n : l.getONeurons())sum += n.getError();	
				else if(!l.isInput()) for(Neuron n : l.getNeurons())sum += n.getError();
				totalsum += Math.pow(sum, 2);
			}
			nn.setGlobalError(totalsum/2);
			s.setTotalGlobalError(s.getTotalGlobalError() + totalsum/2);
		}		
	}
	public static double Sigmoid(double d) {
		return 1/(1+Math.exp(d*-1));
	}

}
