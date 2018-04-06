package BackEvolution.Brawlhalla;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import General.Layer;
import General.NeuralNetwork;
import General.Neuron;

public class BrawlhallaNetwork extends NeuralNetwork {
	Controller controller;
	public BrawlhallaNetwork(Layer inputLayer, Layer outputLayer, Class<?> a)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		super(inputLayer, outputLayer, a);
	}
	public BrawlhallaNetwork(NeuralNetwork nn, Class<?> a)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		super(nn, a);
	}
	public void setController(Controller controller) {
		this.controller = controller;
		for(Layer l : layers){
			for(Neuron n:l.getNeurons()) {
				BrawlhallaNeuron bn = (BrawlhallaNeuron) n;
				bn.setController(controller);
			}
		}
	}
	public void addWin() {
		fitness++;
		
	}
	public void resetWins() {
		fitness = 0;
		
	}	

}
