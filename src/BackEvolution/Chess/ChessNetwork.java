package BackEvolution.Chess;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import General.Layer;
import General.NeuralNetwork;

public class ChessNetwork extends NeuralNetwork {

	public ChessNetwork(Layer inputLayer, Layer outputLayer, Class<?> a)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		super(inputLayer, outputLayer, a);
		// TODO Auto-generated constructor stub
	}
	public ChessNetwork(NeuralNetwork nn, Class<?> a)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		super(nn, a);
		// TODO Auto-generated constructor stub
	}

	

}
