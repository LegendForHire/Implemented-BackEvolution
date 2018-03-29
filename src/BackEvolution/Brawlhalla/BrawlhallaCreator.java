package BackEvolution.Brawlhalla;

import General.Layer;
import General.Neuron;
import General.SpecialCreator;

public class BrawlhallaCreator implements SpecialCreator{
	
	public BrawlhallaCreator() {
		
	}
	@Override
	public void NeuronSetup(Neuron no, int j) {
		
		
	}

	@Override
	public void InputOutputcreator(Layer[] copies) {
		// TODO Auto-generated method stub
		String[] outputs = {"A","B","X","Y","Up","Down","Left","Right","RB","LB","RT","LT"};
		for (String output : outputs) {
			copies[1].addNeuron(new BrawlhallaNeuron("press" + output));
			copies[1].addNeuron(new BrawlhallaNeuron("release" + output));
		}	
	}

}
