package BackEvolution.Tester;

import General.DataManager;
import NeuralNetwork.Layer;
import NeuralNetwork.NeuralNetwork;

public class TestNetwork extends NeuralNetwork {

	public TestNetwork(TestLayer inputLayer, TestLayer outputLayer, DataManager data) {
		super(inputLayer, outputLayer, data);
		// TODO Auto-generated constructor stub
	}
	public TestNetwork(NeuralNetwork nn, Class<?> a) {
		super(nn, a);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void updateFitness() {
		// TODO Auto-generated method stub
		
	}

	

}
