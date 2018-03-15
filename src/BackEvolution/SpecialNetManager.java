package BackEvolution;

public interface SpecialNetManager {
	void IterativeSetup();
	void setup();
	void BackIterationHandling();
	void EvolveSetup();
	void EvolveTeardown();
	void save();
	public void setAct();
	String saveInput(Neuron in);
	String saveOutput(Neuron out);
	String saveMetaData(NeuralNetwork nn);
}
