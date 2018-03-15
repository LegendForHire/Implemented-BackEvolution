package BackEvolution;

public interface SpecialCreator {
	//after neurons are created, use this to give them any additional dat you might need to feed them
	public void NeuronSetup(Neuron no, int j);
	//creates your input and output neurons on each startup
	//copies[0] is used for input neurons and copies[1] is used for output neurons
	public void InputOutputcreator(Layer[] copies);
}
