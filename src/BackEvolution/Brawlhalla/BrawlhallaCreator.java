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
		// TODO Auto-generated method stub
		String[] outputs = {"A","B","X","Y","Up","Down","Left","Right","RB","LB","RT","LT"};
		for (String output : outputs) {
			copies[1].addNeuron(new BrawlhallaNeuron("press" + output));
			copies[1].addNeuron(new BrawlhallaNeuron("release" + output));
		}
		Game g = s.getGame();
		for (int i = 0; i < g.getNumLegends()*2 + g.getNumWeapons()*2 + g.getNumStages()*2 + 6; i++) {
			copies[0].addNeuron(new BrawlhallaNeuron(""+i));
		}
	}

}
