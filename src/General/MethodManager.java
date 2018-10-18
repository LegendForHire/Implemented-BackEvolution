package General;

public abstract class MethodManager {
	
	public MethodManager() {
		
	}
	//This runs before RunNetworks is called and setups up the single
	//use items for further calls to this class item.
	public abstract void setup(DataManager data);
	//this is called when evolve begins.
	public abstract void EvolveSetup(DataManager data);
	//this is called after evolve finishes
	public abstract void EvolveTeardown(DataManager data);
	// This is called at the beginning of each iteration of learning
	// this is for data that needs to be reset or generated on each iteration of learning
	public abstract void BackpropagationSetup(DataManager data);
	// This runs before the backpropagation step is called each time
	// this is for data that needs to be reset or generated on each iteration of backpropagtion
	public abstract void BackIterationHandling(DataManager data);
	//this is the algorithm to determine if each output neuron should have activated or not.
	//This is where you will use the data you set up in BackIterationHandling
	public abstract void setAct(DataManager data);
	//override this method if your input neuron positions could change between loads
	// give each neuron a unique name based on its unique data
	//make sure your returns do not include colons or semicolons or new lines
	public String saveInput(Neuron in, DataManager data) {
		return "" + in.getNumber();
	}
	//override this method if your output neuron positions could change between loads
	// give each neuron a unique name based on its unique data
	//make sure your returns do not include colons or semicolons or new lines
	public String saveOutput(Neuron out, DataManager data) {
		return "" + out.getNumber();
	}
	//override this method if you have additional information you want
	// do not include new lines here
	public String saveMetaData(NeuralNetwork nn, DataManager data) {
		return "";
	}
	// Setup threads and data you need to startup before your networks are run. returns your implemntation of data manager
	public abstract DataManager SetupStartup();
	//setup threads that track additional information from your neural networks
	public abstract void AfterStartup(DataManager data);
	//after neurons are created, use this to give them any additional data you might need to feed them
	public abstract void NeuronSetup(Neuron no, int j, DataManager data);
	//creates your input and output neurons on each startup
	//copies[0] is used for input neurons and copies[1] is used for output neurons
	public abstract void InputOutputcreator(Layer[] copies, DataManager data);
}
