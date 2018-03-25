package BackEvolution.Trader;
/**
 * Wallet.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
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
