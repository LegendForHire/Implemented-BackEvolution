package BackEvolution.Trader;

import java.lang.reflect.InvocationTargetException;

import General.Layer;

public class TraderLayer extends Layer {
	
	public TraderLayer() {
		super(false,false);
	}
	public TraderLayer(boolean i, boolean o) {
		super(i,o);
	}
	public TraderLayer(TraderNeuron n, boolean i, boolean o) {
		super(n,i,o);
	}
	public TraderLayer(TraderLayer l) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		super(l,new TraderNeuron().getClass());
	}
}
