import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class InputNeuron extends Neuron {
	// the method used to get the data for the input neuron.
	private Method method;
	//the market to call the method on
	private Market market;
	// the data point to get for the market
	private int selector;
	public InputNeuron(Method m){
		genes = new ArrayList<Gene>();
		method = m;
		market = null;
		selector= 0;
		value = 0;
	}
	public InputNeuron(InputNeuron n) {
		genes = n.getGenes();
		method = n.getInputMethod();
		market = n.getMarket();
		selector = n.getSelector();
		value = 0;
		number = n.getNumber();
		layernumber = n.getLayernumber();
	}
	public InputNeuron(Method m, Market market, int selected){
		genes = new ArrayList<Gene>();
		method = m;
		this.market = market;
		selector = selected;
		value = 0;
	}
	
	public Market getMarket(){
		return market;
	}
	public int getSelector(){
		return selector;
	}
	public Method getInputMethod(){
		return method;	
	}
	public void updateMethod(Method m){
		method = m;
	}
	//calls the input method with the market and data selector as its inputs.
	public double input() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			Double input = (Double) method.invoke(market,selector);
			if (input == null) return 0.0;
			return input;
	
	}
	
}
