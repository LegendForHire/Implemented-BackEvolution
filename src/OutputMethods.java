import java.io.IOException;
import java.util.ArrayList;
	//holds the output methods for neural network creation. and running.
public class OutputMethods {
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
	public int fakeMeth(){
		return 1;
	}
}
