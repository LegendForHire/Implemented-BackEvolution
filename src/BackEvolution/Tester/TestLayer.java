package BackEvolution.Tester;

import java.lang.reflect.InvocationTargetException;

import NeuralNetwork.Layer;

public class TestLayer extends Layer {

	public TestLayer(Layer l, Class<?> a) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super(l, a);
		// TODO Auto-generated constructor stub
	}
	public TestLayer(boolean isInput, boolean isOutput) {
		super(isInput, isOutput);
		// TODO Auto-generated constructor stub
	}

	

}
