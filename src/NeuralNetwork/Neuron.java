package NeuralNetwork;
/**
 * Neuron.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
import java.util.ArrayList;


public abstract class Neuron {
	protected String method;
	public boolean shouldAct;
	protected ArrayList<Gene> genes;
	protected double value;
	protected int number;
	protected int layernumber;
	protected ArrayList<Gene> input;
	protected double lastinput;
	private double error;
	
	public Neuron(){
		genes = new ArrayList<Gene>();
		input = new ArrayList<Gene>();
		value = 0;
		method = "";
	}
	public Neuron(String method){
		genes = new ArrayList<Gene>();
		input = new ArrayList<Gene>();
		value = 0;
		this.method = method;
	}
	public Neuron(Neuron n) {
		genes = n.getGenes();
		input = n.getInputs();
		value = 0;
		number = n.getNumber();
		layernumber = n.getLayernumber();
		method = n.getMethod();
	}
	public String getMethod(){
		return method;	
	}
	public void updateMethod(String m){
		method = m;
	}
	public void AddGenes(Gene gene){
		genes.add(gene);
		gene.setInput(this);
	}
	public void RemoveGenes(Gene gene){
		genes.remove(gene);
	}
	public ArrayList<Gene> getGenes(){
		return genes;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getLayernumber() {
		return layernumber;
	}
	public void setLayernumber(int layernumber) {
		this.layernumber = layernumber;
	}
	public void cleargenes(){
		genes = new ArrayList<Gene>();
	}
	public void addInput (Gene g){
		input.add(g);
	}
	public ArrayList<Gene> getInputs(){
		return input;
	}
	public double getLast(){
		return lastinput;
	}
	public void setLast(double d){
		lastinput = d;
	}
	public void setError(double d) {
		error = d;
		
	}
	public double getError(){
		return error;
	}
	public void clearInputs() {
		input = new ArrayList<Gene>();
		
	}
	public abstract void invoke();
	public void setActive (boolean b){
		shouldAct = b;
	}
}
