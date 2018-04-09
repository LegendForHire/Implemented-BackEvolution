package BackEvolution.Chess;

import java.lang.reflect.InvocationTargetException;

import General.Layer;
import General.Neuron;

public class ChessLayer extends Layer {

	public ChessLayer(Neuron neuron, boolean isInput, boolean isOutput) {
		super(neuron, isInput, isOutput);
		// TODO Auto-generated constructor stub
	}
	public ChessLayer(Layer l, Class<?> a) throws InstantiationException, IllegalAccessException,
		IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super(l, a);
		// TODO Auto-generated constructor stub
	}
	public ChessLayer(boolean isInput, boolean isOutput) {
		super(isInput, isOutput);
		// TODO Auto-generated constructor stub
	}

	

	

}
