package BackEvolution.Trader;

import java.io.IOException;
import java.util.ArrayList;
import General.Neuron;

public class TraderNeuron extends Neuron{
	private Market market;
	private int selector;
	private ArrayList<Wallet> wallets;
	private long lastmethod;
	public TraderNeuron() {
		super();
	}
	public TraderNeuron(String method, Market market, int selector){
		super(method);
		this.market = market;
		this.selector = selector;
	}
	public TraderNeuron(TraderNeuron n){
		super(n);
		market = n.getMarket();
		selector = n.getSelector();
	}
	@SuppressWarnings("unused")
	private ArrayList<Wallet> getWallets() {
		return wallets;
	}
	public void updateWallets(ArrayList<Wallet> w){
		wallets = w;
	}
	
	public Market getMarket(){
		return market;
	}
	public int getSelector() {
		return selector;
	}
	public void setSelector(int selector) {
		this.selector = selector;
	}
	@Override
	public void invoke(){
		lastmethod = System.currentTimeMillis();
		if (System.currentTimeMillis() - lastmethod > 60000){
			if(method.equals("sell")) {
				try {
					sell(market, selector, wallets);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				try {
					buy(market, selector, wallets);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		else if (method.equals("input")) {
			try {
				setValue(market.getData(selector));
			} catch (IOException e) {
				setValue(0);
			}
		}
	}
	public static void buy(Market market, double ammount, ArrayList<Wallet> wallets) throws IOException{
		String[] walletnames = market.getMarketName().split("-");
		Wallet to = null;
		Wallet from = null;
		for (Wallet w: wallets){
			if (w.getName().equals(walletnames[0])){
				from = w;
			}
			if (w.getName().equals(walletnames[1])){
				to = w;
			}
		}
		double purchase = from.getAmmount()*(ammount/5);
		from.withdraw(purchase);
		to.deposit((purchase/market.getData(6))*.9975);		
	}
	public static void sell(Market market, double ammount, ArrayList<Wallet> wallets) throws IOException{
		String[] walletnames = market.getMarketName().split("-");
		Wallet to = null;
		Wallet from = null;
		for (Wallet w: wallets){
			if (w.getName().equals(walletnames[0])){
				to = w;
			}
			if (w.getName().equals(walletnames[1])){
				from = w;
			}
		}
		double purchase = from.getAmmount()*(ammount/5);
		from.withdraw(purchase);
		to.deposit((purchase*market.getData(5))*.9975);		
	}
}
