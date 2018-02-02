
public class Wallet {
	private String name;
	private double ammount;
	public Wallet(String named){
		name = named;
		ammount = 0;
	}
	public Wallet(String named, double value){
		name = named;
		ammount = value;
	}
	public void deposit(double value){
		ammount += value;
	}
	public void withdraw(double value){
		ammount -= value;
	}
	public String getName(){
		return name;
	}
	public double getAmmount(){
		return ammount;
	}
}
