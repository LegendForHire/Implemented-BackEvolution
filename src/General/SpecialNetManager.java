package General;

// When you name an implemntation of this, name it with respect to your package which should be
//BackEvolution.TYPE replace "Special" with what you have named TYPE
public interface SpecialNetManager {
	
	//This runs before RunNetworks is called and setups up the single
	//use items for further calls to this class item.
	void setup();
	//This is colled before the evolve step each time
	//it is used to set up data for the evolution step
	default String saveInput(Neuron in) {
		return "" + in.getNumber();
	}
	//override this method if your output neuron positions could change between loads
	// give each neuron a unique name based on its unique data
	//make sure your returns do not include colons or semicolons or new lines
	default String saveOutput(Neuron out) {
		return "" + out.getNumber();
	}
	//override this method if you have additional information you want
	// do not include new lines here
	default String saveMetaData(NeuralNetwork nn) {
		return "";
	}
}
