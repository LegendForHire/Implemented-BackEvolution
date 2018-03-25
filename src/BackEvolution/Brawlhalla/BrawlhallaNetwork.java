package BackEvolution.Brawlhalla;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import General.Layer;
import General.NeuralNetwork;

public class BrawlhallaNetwork extends NeuralNetwork {
	public BrawlhallaNetwork(Layer inputLayer, Layer outputLayer, Class<?> a)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		super(inputLayer, outputLayer, a);
		// TODO Auto-generated constructor stub
	}
	public BrawlhallaNetwork(NeuralNetwork nn, Class<?> a)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		super(nn, a);
		// TODO Auto-generated constructor stub
	}

	

}
