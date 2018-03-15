package BackEvolution;

public interface SpecialCreator {
	public void NeuronSetup(Neuron no, int j);
	public void load(NeuralNetwork nno, String[] netData);
	public void InputOutputcreator(Layer[] copies);
}
