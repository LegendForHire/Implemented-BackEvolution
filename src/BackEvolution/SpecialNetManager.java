package BackEvolution;

public interface SpecialNetManager {
	void IterativeSetup();
	void setup();
	void BackIterationHandling();
	void EvolveSetup();
	void EvolveTeardown();
	void save();
	public void setAct();
}
