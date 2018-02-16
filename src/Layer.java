import java.util.ArrayList;

public class Layer {
	private ArrayList<Neuron> neurons;
	private boolean input;
	private boolean output;
	private ArrayList<InputNeuron> ineurons;
	private ArrayList<OutputNeuron> oneurons;
	private int number;
	public Layer(boolean isInput, boolean isOutput){
		output = isOutput;
		input = isInput;
		if(input){
			ineurons = new ArrayList<InputNeuron>();
		}
		else if(output){
			oneurons = new ArrayList<OutputNeuron>();
		}
		else{
			neurons = new ArrayList<Neuron>();
		}
	}
	public Layer(Neuron neuron,boolean isInput, boolean isOutput){
		output = isOutput;
		input = isInput;
		neurons = new ArrayList<Neuron>();
		neuron.setNumber(1);
		neuron.setLayernumber(number);
		neurons.add(neuron);
	}
	public Layer(OutputNeuron oneuron,boolean isInput, boolean isOutput){
		output = isOutput;
		input = isInput;
		oneurons = new ArrayList<OutputNeuron>();
		oneuron.setNumber(1);
		oneuron.setLayernumber(number);
		neurons.add(oneuron);
	}
	public Layer(InputNeuron ineuron,boolean isInput, boolean isOutput){
		output = isOutput;
		input = isInput;
		ineurons = new ArrayList<InputNeuron>();
		ineuron.setNumber(1);
		ineuron.setLayernumber(number);
		ineurons.add(ineuron);
	}
	public Layer(Layer l){
		neurons = new ArrayList<Neuron>();
		ineurons = new ArrayList<InputNeuron>();
		oneurons = new ArrayList<OutputNeuron>();
		if(l.isInput()){
			for (InputNeuron n: l.getINeurons()){
				InputNeuron n2 = new InputNeuron(n);
				ineurons.add(n2);
				n2.setNumber(ineurons.size());
				n2.setLayernumber(number);
			}
		}
		else if(l.isOutput()){
			for (OutputNeuron n: l.getONeurons()){
				OutputNeuron n2 = new OutputNeuron(n);
				oneurons.add(n2);
				n2.setNumber(oneurons.size());
				n2.setLayernumber(number);
			}
		}
		else{
			for (Neuron n: l.getNeurons()){
				Neuron n2 = new Neuron(n);
				neurons.add(n2);
				n2.setNumber(neurons.size());
				n2.setLayernumber(number);
			}	
		}
		output = l.isOutput();
		input = l.isInput();
		number = l.getNumber();
	}
	public ArrayList<InputNeuron> getINeurons() {
		return ineurons;
	}
	public void addOutputNeuron(OutputNeuron n){
		oneurons.add(new OutputNeuron(n));
		oneurons.get(oneurons.size()-1).setNumber(oneurons.size());
	}
	public void addInputNeuron(InputNeuron n){
		ineurons.add(new InputNeuron(n));
		ineurons.get(ineurons.size()-1).setNumber(ineurons.size());
	}
	public void addNeuron(Neuron n){
		neurons.add(new Neuron(n));
		neurons.get(neurons.size()-1).setNumber(neurons.size());
	}
	public ArrayList<OutputNeuron> getONeurons(){
		return oneurons;
	}
	public ArrayList<Neuron> getNeurons(){
		return neurons;
	}
	public boolean isInput(){
		return input;
	}
	public boolean isOutput(){
		return output;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
		if (output) for(OutputNeuron n : oneurons)n.setLayernumber(number);
		else if (input) for(InputNeuron n : ineurons)n.setLayernumber(number);
		else for(Neuron n : neurons)n.setLayernumber(number);

	}
}
