package BackEvolution.Tester;

import java.util.ArrayList;

import NeuralNetwork.Gene;
import NeuralNetwork.Neuron;

public class TestNeuron extends Neuron {
	public TestNeuron(){
		super();
	}
	public TestNeuron(String method){
		super(method);
	}
	public TestNeuron(TestNeuron n) {
		super(n);
	}
	@Override
	public void invoke() {
		// TODO Auto-generated method stub
		
	}
}
