package General;

import java.util.ArrayList;

public class Species {
	private ArrayList<NeuralNetwork> members;

	public Species(NeuralNetwork network) {
		members = new ArrayList<NeuralNetwork>();
		members.add(network);
	}

	public ArrayList<NeuralNetwork> getNetworks() {
		return members;
	}
	
	
}
