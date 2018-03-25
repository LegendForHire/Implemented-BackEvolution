package BackEvolution.Brawlhalla;

import java.lang.reflect.InvocationTargetException;

import General.Layer;
import General.Neuron;

public class BrawlhallaLayer extends Layer{
	
	public BrawlhallaLayer(Neuron neuron, boolean isInput, boolean isOutput) {
		super(neuron, isInput, isOutput);
		// TODO Auto-generated constructor stub
	}
	public BrawlhallaLayer(Layer l, Class<?> a) throws InstantiationException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super(l, a);
		// TODO Auto-generated constructor stub
	}
	public BrawlhallaLayer(boolean isInput, boolean isOutput) {
		super(isInput, isOutput);
		// TODO Auto-generated constructor stub
	}

	
	

}
