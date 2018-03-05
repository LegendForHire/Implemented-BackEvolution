import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class OutputNeuron extends Neuron {
	private String method;
	private double activation;
	private Market market;
	private int selector;
	private ArrayList<Wallet> wallets;
	private long lastmethod;
	public boolean shouldAct;
	private double error;
	public OutputNeuron(String m, double d){
		genes = new ArrayList<Gene>();
		method = m;
		activation = d;
		market = null;
		selector = 0;
		wallets = null;
		value = 0;
	}
	public OutputNeuron(OutputNeuron n) {
		genes = new ArrayList<Gene>();
		method = n.getOutputMethod();
		activation = n.getActivation();
		market = n.getMarket();
		selector = n.getSelector();
		wallets = n.getWallets();
		value = 0;
		number = n.getNumber();
		layernumber = n.getLayernumber();
	}
	private ArrayList<Wallet> getWallets() {
		return wallets;
	}
	public OutputNeuron(String string, double d, Market market, int selected){
		genes = new ArrayList<Gene>();
		method = string;
		activation = d;
		this.market = market;
		selector = selected;
		wallets = null;
		value = 0;
		
	}
	public void updateWallets(ArrayList<Wallet> w){
		wallets = w;
	}
	public String getOutputMethod(){
		return method;	
	}
	public void updateMethod(String m){
		method = m;
	}
	public Market getMarket(){
		return market;
	}
	public double getActivation(){
		return activation;
	}
	public void updateActivation(double d){
		activation = d;
	}
	public int getSelector() {
		return selector;
	}
	public void setSelector(int selector) {
		this.selector = selector;
	}
	public void invoke() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException{
		lastmethod = System.currentTimeMillis();
		if (System.currentTimeMillis() - lastmethod > 60000){
			if(method.equals("sell")) {
				OutputMethods.sell(market, selector, wallets);
			}
			else {
				OutputMethods.buy(market, selector, wallets);
			}
			
		}
	}
	public void setActive (boolean b){
		shouldAct = b;
	}
	public void setError(double d) {
		error = d;
		
	}
	public double getError(){
		return error;
	}
	
	
}
