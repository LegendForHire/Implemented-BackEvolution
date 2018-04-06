package BackEvolution.Brawlhalla;

import General.Layer;
import General.Neuron;
import General.SpecialCreator;

public class BrawlhallaCreator implements SpecialCreator{
	BrawlhallaSingleton s = BrawlhallaSingleton.getInstance();
	public BrawlhallaCreator() {
		
	}
	@Override
	public void NeuronSetup(Neuron no, int j) {
				
	}

	@Override
	public void InputOutputcreator(Layer[] copies) {
		String[] outputs = {"A","B","X","Y","Up","Down","Left","Right","RB","LB","RT","LT"};
		for (String output : outputs) {
			copies[1].addNeuron(new BrawlhallaNeuron("press" + output));
			copies[1].addNeuron(new BrawlhallaNeuron("release" + output));
		}
		for (int i = 0; i < s.getLegends().size()*2 + s.getWeapons().size()*4 + s.getStages().size()*2 + 6; i++) {
			copies[0].addNeuron(new BrawlhallaNeuron(""+i));
		}
	}

}
