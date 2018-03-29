package BackEvolution.Brawlhalla;

import General.Neuron;

public class BrawlhallaNeuron extends Neuron {
	Controller controller;
	public BrawlhallaNeuron(){
		super();
		//TODO
	}
	public BrawlhallaNeuron(String method){
		super(method);
		//TODO
	}
	public BrawlhallaNeuron(Neuron n) {
		super(n);
		//TODO
	}
	@Override
	public void invoke(){
		if(method.contains("press")) {
			controller.press(method.substring(5));
		}
		else if(method.contains("release")) {
			controller.press(method.substring(7));
		}
		else {
			setValue(BrawlhallaSingleton.getInstance().getGame().getData(controller,Integer.parseInt(method)));
		}
	}
	public void setController(Controller controller) {
		this.controller = controller;
		
	}
}
