import java.util.ArrayList;

public class Neuron {
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
	}
	public Neuron(Neuron n) {
		genes = n.getGenes();
		input = n.getInputs();
		value = 0;
		number = n.getNumber();
		layernumber = n.getLayernumber();
	}
	public void AddGenes(Gene gene){
		genes.add(gene);
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
}
