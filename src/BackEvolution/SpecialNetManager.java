package BackEvolution;

public interface SpecialNetManager {
	void IterativeSetup();
	void setup();
	void BackIterationHandling();
	void EvolveSetup();
	void EvolveTeardown();
	public void setAct();
	default String saveInput(Neuron in) {
		return "" + in.getNumber();
	}
	default String saveOutput(Neuron out) {
		return "" + out.getNumber();
	}
	default String saveMetaData(NeuralNetwork nn) {
		return "";
	}
}
