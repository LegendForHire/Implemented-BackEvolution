import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class NeuralNetwork implements Comparable<NeuralNetwork>{
	private ArrayList<Layer> layers;
	private double fitness;
	public ArrayList<Wallet> wallets;
	public Market[] markets;
	private int age;
	private long created;
	private double globalError;
	public NeuralNetwork(Layer inputLayer, Layer outputLayer, Market[] markets) throws IOException{
		layers = new ArrayList<Layer>();
		layers.add(new Layer(inputLayer));
		layers.get(0).setNumber(1);
		layers.add(new Layer(outputLayer));
		layers.get(1).setNumber(2);
		fitness = 0;
		//loads the currencies from the bittrex api.
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
		this.markets = markets;
		updateFitness();
		age = 0;
		setCreated(System.currentTimeMillis());
	}
	//made to clone neural networks. didn't work as intended, but not deleted in case used elsewhere for other purpose.
	public NeuralNetwork(NeuralNetwork nn) throws IOException {
		ArrayList<Layer> layers = nn.getLayers();
		for (Layer l : layers){
			this.layers.add(new Layer(l));
		}
		restartWallets();
		updateFitness();
		age = 0;
		markets = nn.getMarkets();		
	}
	
	public void setMarkets(Market[] markets){
		this.markets= markets;
	}
	public Market[] getMarkets(){
		return markets;
	}
	public void addLayer(Layer layer){
		layers.add(layers.size()-1, layer);
		layers.get(layers.size()-2).setNumber(layers.size()-1);
		layers.get(layers.size()-1).setNumber(layers.size());
	}
	public ArrayList<Layer> getLayers(){
		return layers;
	}
	public double getFitness(){
		return fitness;
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
	//resets the wallets to a default state
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
		for (OutputNeuron n : layers.get(layers.size()-1).getONeurons()){
			n.updateWallets(wallets);
		}
		}
		catch (Exception e){
			long t = System.currentTimeMillis();
			while (System.currentTimeMillis() - t < 10000);
			restartWallets();
		}
	}
	//finds the fitness in amount of bitcoin.
	public void updateFitness() throws IOException {
		fitness = 0;
		for (Wallet w : wallets){
			double amt = w.getAmmount();
			if (w.getName().equals("BTC")){
				fitness+= amt;
			}
			else if(!w.getName().equals("XBB")&&!w.getName().equals("HKG")){
				for (Market market : markets){
					if (market.getMarketName().equals("BTC-" + w.getName())){
						fitness += amt*market.getData(3);
					}
				
				}
			}
		}
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	@Override
	public int compareTo(NeuralNetwork n) {
        if (n == null) return -1;
		if(this.getFitness()>n.getFitness()){
			return -1;
		}
		if(this.getFitness()==n.getFitness()){
			int nn1 = 0;
			int nn2 = 0;
			for(Layer l : n.getLayers()){
				if (l.isInput()){
					for(InputNeuron neuron : l.getINeurons()){
						nn1 += neuron.getGenes().size();
					}
				}
				else if(!l.isOutput()){
					for(Neuron neuron : l.getNeurons()){
						nn1 += neuron.getGenes().size();
					}
				}
			}
			for(Layer l : this.getLayers()){
				if (l.isInput()){
					for(InputNeuron neuron : l.getINeurons()){
						nn2 += neuron.getGenes().size();
					}
				}
				else if(!l.isOutput()){
					for(Neuron neuron : l.getNeurons()){
						nn2 += neuron.getGenes().size();
					}
				}
			}
			if(nn1>nn2)return 1;
			if(nn2>nn1)return -1;
			return 0;
		}
		return 1;
	}
	public long getCreated() {
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public void setGlobalError(double totalsum) {
		// TODO Auto-generated method stub
		globalError = totalsum;
	}
	public double getGlobalError(){
		return globalError;
	}
	public void clearInputArrays() {
		for(Layer l : layers){
			if (l.isOutput()){
				for(OutputNeuron n : l.getONeurons()){
					n.clearInputs();
				}
			}
			else if (!l.isInput()){
				for(Neuron n : l.getNeurons()){
					n.clearInputs();
				}
			}
		}
		
	}
	
}
